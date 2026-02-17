package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.EpisodeLinkOptions
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseSourceDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.ReleaseRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class ReleaseGateway(
    val repository: ReleaseRepository,
) {

    fun save(release: Release) = release.let(this::toDocument).let(repository::save)

    @Cacheable("releasesCache")
    fun findAll(pageRequest: PageRequest): Page<Release> {
        return repository.findAll(pageRequest).map(this::fromDocument)
    }

    fun findByTitle(pageRequest: PageRequest, query: String): Page<Release> {
        return repository.findByTitleContainingIgnoreCase(query, pageRequest).map(this::fromDocument)
    }

    fun findByAnimeIdOrderByEpisodeDesc(animeId: String): List<Release> {
        return repository.findByAnimeIdOrderByTitleDesc(animeId).map(this::fromDocument)
    }

    fun findByAnimeIdAndEpisodeNumber(animeId: String, episodeNumber: String): List<Release> {
        return repository.findByAnimeIdAndEpisode(animeId, episodeNumber).map(this::fromDocument)
    }

    fun toDocument(release: Release): ReleaseDocument {
        return ReleaseDocument(
            id = release.id,
            title = release.title,
            episode = release.animeEpisode,
            animeId = release.animeId,
            animeSlug = release.animeSlug,
            animeName = release.animeName,
            animeType = release.animeType,
            animeImage = release.animeImageUrl,
            animeEpisode = release.animeEpisode,
            sources = release.options?.map { ReleaseSourceDocument(it.name, it.url) },
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )
    }

    fun fromDocument(document: ReleaseDocument): Release {
        return Release(
            id = document.id,
            title = document.title,
            animeSlug = document.animeSlug ?: document.anime?.source?.jikan?.url!!.split("/").last().lowercase(),
            animeName = document.animeName ?: document.anime?.name ?: "",
            animeEpisode = document.animeEpisode ?: document.episode,
            animeType = if (document.animeType == "TV") "Episódio" else (document.animeType ?: "Episódio"),
            animeImageUrl = document.animeImage ?: document.anime?.image,
            animeId = document.animeId,
            options = document.sources!!.map { EpisodeLinkOptions(it.url, it.title) }.toMutableList(),
        )
    }

//    @EventListener(ApplicationReadyEvent::class)
//    fun migrate() {
//        val documents = repository.findAll()
//
//        documents.map { anime ->
//            val releasesDuplicates = documents.parallelStream().filter { it.animeName == anime.animeName && it.animeEpisode == anime.animeEpisode }
//
//            if (releasesDuplicates.count() > 1) {
//                println("[${anime.animeName} ${anime.animeEpisode} ] This anime has more de one release per episode")
//            }
//        }
//
////        repository.saveAll(documents)
//    }

}