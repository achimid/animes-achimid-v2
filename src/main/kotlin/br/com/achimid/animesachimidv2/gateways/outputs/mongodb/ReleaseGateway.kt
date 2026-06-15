package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.EpisodeLinkOptions
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseSourceDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.ReleaseRepository
import br.com.achimid.animesachimidv2.utils.padLeft
import br.com.achimid.animesachimidv2.utils.unpadLeft
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class ReleaseGateway(
    val repository: ReleaseRepository,
) {

    fun save(release: Release) = release.let(this::toDocument).let(repository::save)

    fun findById(id: String): Release? =
        repository.findById(id).map(this::fromDocument).orElse(null)

    fun updateAnimeImage(animeId: String, imageUrl: String) =
        repository.updateAnimeImageByAnimeId(animeId, imageUrl)

    @Cacheable("releasesCache")
    fun findAll(pageRequest: PageRequest): Page<Release> {
        return repository.findVisible(pageRequest).map(this::fromDocument)
    }

    fun findByTitle(pageRequest: PageRequest, query: String): Page<Release> {
        return repository.findVisibleByTitleContaining(query, pageRequest).map(this::fromDocument)
    }

    fun findByAnimeIdOrderByEpisodeDesc(animeId: String): List<Release> {
        return repository.findVisibleByAnimeIdOrderByEpisodeDesc(animeId).map(this::fromDocument)
    }

    fun findByAnimeIdAndEpisodeNumber(animeId: String, episodeNumber: String): List<Release> {
        return repository.findByAnimeIdAndEpisode(animeId, episodeNumber.padLeft()!!).map(this::fromDocument)
    }

    @Cacheable("statsCache", key = "'releasesToday'")
    fun countToday(): Long {
        val startOfDay = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant()
        return repository.countVisibleByCreatedAtAfter(startOfDay)
    }

    fun findNeedingReview(pageRequest: PageRequest): Page<Release> =
        repository.findNeedingReview(pageRequest).map(this::fromDocument)

    fun toDocument(release: Release): ReleaseDocument {
        return ReleaseDocument(
            id = release.id,
            title = release.title,
            episode = release.animeEpisode.padLeft(),
            animeId = release.animeId,
            animeSlug = release.animeSlug,
            animeName = release.animeName,
            animeType = release.animeType,
            animeImage = release.animeImageUrl,
            animeEpisode = release.animeEpisode.padLeft(),
            animeStreamUrl = release.animeStreamUrl,
            sources = release.options?.map { ReleaseSourceDocument(it.name, it.url) },
            hidden = release.hidden,
            matchScore = release.matchScore,
            needsReview = release.needsReview,
            rawSearchTitle = release.rawSearchTitle,
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
            animeEpisode = document.animeEpisode.unpadLeft() ?: document.episode.unpadLeft(),
            animeType = if (document.animeType == "TV") "Episódio" else (document.animeType ?: "Episódio"),
            animeImageUrl = document.animeImage ?: document.anime?.image,
            animeStreamUrl = document.animeStreamUrl,
            animeId = document.animeId,
            options = document.sources!!.map { EpisodeLinkOptions(it.url, it.title) }.toMutableList(),
            hidden = document.hidden,
            matchScore = document.matchScore,
            needsReview = document.needsReview,
            rawSearchTitle = document.rawSearchTitle,
        )
    }

}