package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.NextEpisode
import br.com.achimid.animesachimidv2.gateways.outputs.http.AniListAPIGateway
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

/**
 * Busca o próximo episódio de um anime na AniList (FUNC-13). Cacheado e tolerante a falha
 * (se a AniList estiver indisponível ou o anime não estiver em exibição, retorna null).
 */
@Component
class FindNextEpisodeUseCase(
    private val aniListAPIGateway: AniListAPIGateway
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable("nextEpisodeCache")
    fun execute(animeId: String): NextEpisode? {
        val malId = animeId.toIntOrNull() ?: return null
        return try {
            aniListAPIGateway.findNextAiringEpisode(malId)
        } catch (ex: Exception) {
            logger.warn("Falha ao consultar próximo episódio na AniList para anime $animeId", ex)
            null
        }
    }
}
