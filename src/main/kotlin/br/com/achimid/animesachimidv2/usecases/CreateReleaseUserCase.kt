package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.EpisodeLinkOptions
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegrationExecutionResult
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class CreateReleaseUserCase(
    val releaseGateway: ReleaseGateway,
    val searchUseCase: SearchUseCase,
    val afterCreateReleaseUserCase: AfterCreateReleaseUserCase
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @Caching(evict = [
        CacheEvict("releasesCache", allEntries = true),
        CacheEvict("statsCache", allEntries = true),
        CacheEvict("animeCache", allEntries = true),
    ])
    fun execute(result: CallbackIntegrationExecutionResult) {
        val searchResult = searchUseCase.execute(result.anime!!)
        val anime = searchResult.anime

        logger.info("Search: ${result.anime} -> Found: ${anime.name} (score: ${searchResult.score}, needsReview: ${searchResult.needsReview})")

        val release = releaseGateway.findByAnimeIdAndEpisodeNumber(anime.id, result.episode!!)
            .firstOrNull() ?: Release(
            title = result.title,
            animeId = anime.id,
            animeSlug = anime.slug,
            animeName = anime.name,
            animeType = anime.getTypeDescription(),
            animeEpisode = result.episode,
            animeImageUrl = anime.imageUrl,
            animeStreamUrl = anime.streamingUrl,
            matchScore = searchResult.score,
            needsReview = searchResult.needsReview,
            rawSearchTitle = searchResult.rawTitle,
        )

        afterCreateReleaseUserCase.execute(release, anime, result.from)

        if (release.options!!.any { it.name == result.from }) return

        release.options.add(EpisodeLinkOptions(result.url, result.from))

        releaseGateway.save(release).let { logger.info("Released created with success: [${release.title}]") }
    }

}
