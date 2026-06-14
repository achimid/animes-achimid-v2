package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.NextEpisode
import br.com.achimid.animesachimidv2.gateways.outputs.http.anilist.AniListAPIClient
import br.com.achimid.animesachimidv2.gateways.outputs.http.anilist.AniListRequest
import org.springframework.stereotype.Component

@Component
class AniListAPIGateway(
    private val aniListAPIClient: AniListAPIClient
) {

    private val nextEpisodeQuery = """
        query (${'$'}malId: Int) {
          Media(idMal: ${'$'}malId, type: ANIME) {
            nextAiringEpisode { episode airingAt timeUntilAiring }
          }
        }
    """.trimIndent()

    /** Próximo episódio de um anime pelo id do MyAnimeList (FUNC-13). */
    fun findNextAiringEpisode(malId: Int): NextEpisode? {
        val response = aniListAPIClient.query(AniListRequest(nextEpisodeQuery, mapOf("malId" to malId)))
        val next = response.data?.media?.nextAiringEpisode ?: return null

        return NextEpisode(
            episode = next.episode ?: return null,
            airingAt = next.airingAt ?: return null,
            timeUntilAiring = next.timeUntilAiring ?: return null,
        )
    }
}
