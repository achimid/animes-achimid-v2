package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.Jikan
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NameDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.AnimeDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.AnimeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.PageRequest.of
import org.springframework.stereotype.Component

@Component
class AnimeGateway(
    val animeRepository: AnimeRepository,
    val namesRepository: NamesRepository,
    val mapper: AnimeDocumentMapper
) {

    fun saveAll(animes: List<Jikan>): List<Anime> = animes
        .map(mapper::toDocument)
        .let(animeRepository::saveAll)
        .also { getAllNames(it).forEach(namesRepository::save) }
        .map(mapper::fromDocument)

    @Cacheable("animesCache")
    fun findAll(pageRequest: PageRequest): Page<Anime> {
        return animeRepository.findAll(pageRequest).map(mapper::fromDocument)
    }

    @Cacheable("recommendationsCache")
    fun findRandom(size: Int = 5): Page<Anime> {
        return animeRepository.findAll(of((0..300).random(), size)).map(mapper::fromDocument)
    }

    @Cacheable("animeSearchCache")
    fun findByName(pageRequest: PageRequest, query: String): Page<Anime> {
        return animeRepository.findByNameContainingIgnoreCase(query, pageRequest).map(mapper::fromDocument)
    }

    fun findLastSlugs(): List<String> = animeRepository.findAll().map { it.slug }

    fun findBySlug(slug: String): Anime? = animeRepository.findBySlug(slug)?.let(mapper::fromDocument)

    fun findById(id: String): Anime? = animeRepository.findById(id).get().let(mapper::fromDocument)

    fun addComment(id: String, comment: AnimeComment): AnimeComment {
        animeRepository.addComment(id, mapper.toDocument(comment))
        return comment
    }

    fun getAllNames(animes: List<AnimeDocument>): List<NameDocument> {
        return animes.flatMap {
            val jikan = it.sources!!.jikan!!

            val names = mutableSetOf(
                NameDocument(jikan.title, it.id!!),
                NameDocument(jikan.titleEnglish ?: "", it.id),
                NameDocument(jikan.titleJapanese ?: "", it.id),
            )

            names.addAll(jikan.titles?.map { title ->
                return@map try {
                    NameDocument((title as HashMap<String, String>).get("title") ?: "", it.id)
                } catch (ex: Exception) {
                    try {
                        NameDocument((title as HashMap<String, String>).get("title") ?: "", it.id)
                    } catch (ex: Exception) {
                        NameDocument((title as String ?: ""), it.id)
                    }

                }
            } ?: emptyList())
            names.addAll(jikan.titleSynonyms?.map { title -> NameDocument(title, it.id) } ?: emptyList())

            return@flatMap names
        }.filter { it.name != "" }
    }

    //        @PostConstruct
    fun migrate() {
//        val animes = animeRepository.findAll().map(mapper::toDocument)
//
//        animeRepository.saveAll(animes)
    }
}