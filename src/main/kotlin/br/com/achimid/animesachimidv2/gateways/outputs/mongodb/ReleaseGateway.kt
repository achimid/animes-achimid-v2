package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.EpisodeLinkOptions
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.ReleaseDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.ReleaseRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReleaseGateway(
    val repository: ReleaseRepository,
) {

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

    fun fromDocument(document: ReleaseDocument): Release {
        return Release(
            id = UUID.randomUUID(),
            animeSlug = document.animeSlug ?: document.anime?.source?.jikan?.url!!.split("/").last().lowercase(),
            animeTitle = document.animeName ?: document.anime?.name ?: "",
            animeNumber = document.episode,
            animeImageUrl = document.animeImage ?: document.anime?.image,
            animeId = document.animeId,
            options = document.sources.map { EpisodeLinkOptions(it.url, it.title) }
        )
    }

//        @PostConstruct
    fun migrate() {
        val documents = repository.findAll().filter{ it.animeId == null }.map {
            val jikan = it.anime!!.source.jikan!!
            val slug = jikan.url!!.split("/").last().lowercase()

            return@map it.copy(
                animeId = jikan.malId!!.toString(),
                animeSlug = slug,
                animeType = it.anime.type,
                animeName = it.anime.name,
                animeImage = it.anime.image,
            )
        }.toList()

        repository.saveAll(documents)
    }

}