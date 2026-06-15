package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.AnimeCreatedEvent
import br.com.achimid.animesachimidv2.domains.CommentStatus
import br.com.achimid.animesachimidv2.domains.Jikan
import br.com.achimid.animesachimidv2.domains.ScheduledAnime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeStatusDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NameDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.AnimeDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.AnimeRepository
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.PageRequest.of
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

data class AnimeSitemapEntry(val slug: String, val updatedAt: Instant?)

@Component
class AnimeGateway(
    val mapper: AnimeDocumentMapper,
    val animeRepository: AnimeRepository,
    val namesRepository: NamesRepository,
    val mongoTemplate: MongoTemplate,
    val eventPublisher: ApplicationEventPublisher,
) {

    fun saveAll(animes: List<Jikan>): List<Anime> {
        val savedDocs = animes.map {
            val existing = animeRepository.findById(it.malId.toString()).getOrNull()
            val isNew = existing == null
            val doc = existing ?: mapper.toDocument(it)
            Pair(isNew, animeRepository.save(mapper.merge(doc, it).copy(jikanSyncedAt = Instant.now())))
        }
        savedDocs.map { it.second }.also { getAllNames(it).forEach(namesRepository::save) }
        return savedDocs.map { (isNew, doc) ->
            mapper.fromDocument(doc).also { anime ->
                if (isNew) eventPublisher.publishEvent(AnimeCreatedEvent(anime))
            }
        }
    }

    fun findRecentlyAdded(limit: Int = 30): List<Anime> =
        animeRepository.findTop30ByOrderByCreatedAtDesc().map(mapper::fromDocument)

    fun save(anime: Anime): Anime = anime
        .let(mapper::toDocument)
        .let(animeRepository::save)
        .let(mapper::fromDocument)

    @Cacheable("animesCache")
    fun findAll(pageRequest: PageRequest): Page<Anime> {
        return animeRepository.findAll(pageRequest).map(mapper::fromDocument)
    }

    @Cacheable("featuredAnimeCache")
    fun findFeatured(): Anime? {
        val now = LocalDate.now()
        val currentSeason = when (now.monthValue) {
            in 1..3 -> "winter"
            in 4..6 -> "spring"
            in 7..9 -> "summer"
            else -> "fall"
        }
        val currentYear = now.year

        // Pega os 20 animes da temporada mais recentemente atualizados e sorteia 1 aleatoriamente
        val result = mongoTemplate.aggregate(
            Aggregation.newAggregation(
                Aggregation.match(
                    Criteria.where("season").`is`(currentSeason)
                        .and("year").`is`(currentYear)
                        .and("imageUrl").ne(null)
                ),
                Aggregation.sort(Sort.Direction.DESC, "updatedAt"),
                Aggregation.limit(20),
                Aggregation.sample(1),
            ),
            "animes", AnimeDocument::class.java
        ).mappedResults.firstOrNull()

        return result?.let(mapper::fromDocument)
    }

    @Cacheable("currentSeasonCache")
    fun findCurrentSeason(limit: Int = 30): List<Anime> {
        val now = LocalDate.now()
        val season = when (now.monthValue) {
            in 1..3 -> "winter"
            in 4..6 -> "spring"
            in 7..9 -> "summer"
            else -> "fall"
        }
        val pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "updatedAt"))
        return animeRepository.findBySeasonAndYear(season, now.year, pageRequest)
            .content.map(mapper::fromDocument)
    }

    @Cacheable("recommendationsCache")
    fun findRandom(size: Int = 5): List<Anime> {
        val agg = Aggregation.newAggregation(Aggregation.sample(size.toLong()))
        return mongoTemplate.aggregate(agg, "animes", AnimeDocument::class.java)
            .mappedResults
            .map(mapper::fromDocument)
    }

    /** Animes que compartilham qualquer uma das tags (gênero/tema), ordenados por score (FUNC-10). */
    fun findByTags(tags: Collection<String>, limit: Int): List<Anime> {
        if (tags.isEmpty()) return emptyList()
        val pageRequest = of(0, limit, Sort.by(Sort.Direction.DESC, "score"))
        return animeRepository.findByTagsIn(tags, pageRequest).content.map(mapper::fromDocument)
    }

    @Cacheable("animeSearchCache")
    fun findByName(pageRequest: PageRequest, query: String): Page<Anime> {
        return animeRepository.findByNameContainingIgnoreCase(query, pageRequest).map(mapper::fromDocument)
    }

    fun findWithFilters(pageRequest: PageRequest, query: String?, genre: String?): Page<Anime> {
        val hasQuery = !query.isNullOrEmpty()
        val hasGenre = !genre.isNullOrEmpty()
        return when {
            !hasGenre && !hasQuery -> findAll(pageRequest)
            !hasGenre             -> findByName(pageRequest, query!!)
            !hasQuery             -> animeRepository.findByTagsIn(listOf(genre!!), pageRequest).map(mapper::fromDocument)
            else                  -> animeRepository.findByTagsInAndNameContainingIgnoreCase(listOf(genre!!), query!!, pageRequest).map(mapper::fromDocument)
        }
    }

    @Cacheable("statsCache", key = "'animeCount'")
    fun count(): Long = animeRepository.count()

    fun findAllSlugs(): List<String> = findSitemapData().map { it.slug }

    fun findSitemapData(): List<AnimeSitemapEntry> {
        val query = BasicQuery("{}", "{ '_id': 0, 'slug': 1, 'updatedAt': 1 }")
        return mongoTemplate.find(query, org.bson.Document::class.java, "animes").map { doc ->
            AnimeSitemapEntry(
                slug = doc.getString("slug") ?: "",
                updatedAt = doc["updatedAt"]?.let {
                    when (it) {
                        is java.util.Date -> it.toInstant()
                        is Instant -> it
                        else -> null
                    }
                }
            )
        }.filter { it.slug.isNotEmpty() }
    }

    fun findBySlug(slug: String): Anime? = animeRepository.findBySlug(slug)
        ?.let(mapper::fromDocument)
        ?.run { copy(comments = comments?.sortedByDescending { it.createdAt }) }

    fun findById(id: String): Anime? = animeRepository.findById(id).getOrNull()?.let(mapper::fromDocument)

    fun addComment(id: String, comment: AnimeComment): AnimeComment {
        animeRepository.addComment(id, mapper.toDocument(comment))
        return comment
    }

    fun findAnimesWithPendingComments(): List<Anime> =
        animeRepository.findByCommentsStatus(CommentStatus.PENDING).map(mapper::fromDocument)

    fun findAnimesWithAnyComments(): List<Anime> =
        animeRepository.findByCommentsNotEmpty().map(mapper::fromDocument)

    fun deleteComment(animeId: String, commentId: String): Boolean {
        val doc = animeRepository.findById(animeId).getOrNull() ?: return false
        val updated = doc.comments?.filter { it.id != commentId } ?: return false
        animeRepository.save(doc.copy(comments = updated))
        return true
    }

    /** Animes em exibição com dia/horário de transmissão (do Jikan) — agenda híbrida (FUNC-08). */
    fun findAiringScheduled(): List<ScheduledAnime> =
        animeRepository.findByStatus(AnimeStatusDocument.AIRING).mapNotNull { doc ->
            val broadcast = doc.sources?.jikan?.broadcast ?: return@mapNotNull null
            val day = broadcast.day ?: return@mapNotNull null
            ScheduledAnime(mapper.fromDocument(doc), day, broadcast.time)
        }

    fun updateCommentStatus(animeId: String, commentId: String, status: CommentStatus): Boolean {
        val doc = animeRepository.findById(animeId).getOrNull() ?: return false
        val comments = doc.comments ?: return false
        val updated = comments.map { if (it.id == commentId) it.copy(status = status) else it }
        animeRepository.save(doc.copy(comments = updated))
        return true
    }

    fun getAllNames(animes: List<AnimeDocument>): List<NameDocument> {
        return animes.flatMap {
            val jikan = it.sources!!.jikan!!

            val names = mutableSetOf(
                NameDocument(name = jikan.title, animeId = it.id!!),
                NameDocument(name = jikan.titleEnglish ?: "", animeId = it.id),
                NameDocument(name = jikan.titleJapanese ?: "", animeId = it.id),
            )

            names.addAll(jikan.titles?.map { title ->
                return@map try {
                    NameDocument(name = (title as HashMap<String, String>).get("title") ?: "", animeId = it.id)
                } catch (_: Exception) {
                    NameDocument(name = (title as String), animeId = it.id)
                }
            } ?: emptyList())
            names.addAll(jikan.titleSynonyms?.map { title -> NameDocument(name = title, animeId = it.id) }
                ?: emptyList())

            return@flatMap names
        }.filter { it.name != "" }
    }

    fun findAllWithoutTranslation(): List<Anime> = animeRepository.findAllWithoutTranslation().map(mapper::fromDocument)

}