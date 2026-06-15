package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class ReassignReleaseUseCase(
    val releaseGateway: ReleaseGateway,
    val animeGateway: AnimeGateway,
    val namesRepository: NamesRepository,
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @Caching(evict = [
        CacheEvict("releasesCache", allEntries = true),
        CacheEvict("statsCache", allEntries = true),
        CacheEvict("animeCache", allEntries = true),
    ])
    fun execute(releaseId: String, correctAnimeSlug: String) {
        val release = releaseGateway.findById(releaseId) ?: return
        val anime = animeGateway.findBySlug(correctAnimeSlug) ?: return

        release.rawSearchTitle?.let { rawTitle ->
            namesRepository.saveConfirmedAlias(rawTitle, anime.id, anime.name)
            logger.info("Alias confirmado salvo: '$rawTitle' → ${anime.name}")
        }

        releaseGateway.save(release.copy(
            animeId = anime.id,
            animeSlug = anime.slug,
            animeName = anime.name,
            animeImageUrl = anime.imageUrl,
            animeStreamUrl = anime.streamingUrl,
            needsReview = false,
            matchScore = 100,
        ))

        logger.info("Release '$releaseId' reatribuído para ${anime.name}")
    }
}
