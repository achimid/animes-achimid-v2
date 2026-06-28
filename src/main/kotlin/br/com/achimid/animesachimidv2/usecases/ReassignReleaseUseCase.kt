package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import br.com.achimid.animesachimidv2.utils.extractBaseTitle
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

private const val BULK_SIMILARITY_THRESHOLD = 95
private const val BULK_MAX_AUTO_REASSIGN = 100

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
    fun execute(releaseId: String, correctAnimeSlug: String): Int {
        val release = releaseGateway.findById(releaseId) ?: return 0
        val anime = animeGateway.findBySlug(correctAnimeSlug) ?: return 0

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

        val mainBaseTitle = extractBaseTitle(release.rawSearchTitle ?: release.title ?: "")
        if (mainBaseTitle.isBlank()) return 0

        val candidates = releaseGateway.findSimilarForReview(mainBaseTitle)
            .filter { it.id != releaseId }
            .take(BULK_MAX_AUTO_REASSIGN)

        var autoMatched = 0
        for (candidate in candidates) {
            val candidateBase = extractBaseTitle(candidate.rawSearchTitle ?: candidate.title ?: "")
            val score = FuzzySearch.ratio(mainBaseTitle, candidateBase)
            if (score >= BULK_SIMILARITY_THRESHOLD) {
                candidate.rawSearchTitle?.let { namesRepository.saveConfirmedAlias(it, anime.id, anime.name) }
                releaseGateway.save(candidate.copy(
                    animeId = anime.id,
                    animeSlug = anime.slug,
                    animeName = anime.name,
                    animeImageUrl = anime.imageUrl,
                    animeStreamUrl = anime.streamingUrl,
                    needsReview = false,
                    matchScore = 100,
                ))
                autoMatched++
                logger.info("Auto-reatribuição: '${candidate.rawSearchTitle}' → ${anime.name} (score=$score)")
            }
        }

        if (autoMatched > 0) logger.info("$autoMatched releases similares reatribuídos automaticamente para ${anime.name}")
        return autoMatched
    }
}
