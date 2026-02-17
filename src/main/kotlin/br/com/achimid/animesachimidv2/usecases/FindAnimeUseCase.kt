package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.EpisodeInfo
import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class FindAnimeUseCase(
    private val animeGateway: AnimeGateway,
    private val releaseGateway: ReleaseGateway,
    private val jikanGateway: JikanAPIGateway
) {

    @Cacheable("animeCache")
    fun execute(idOrSlug: String) : Anime {
        val anime = animeGateway.findBySlug(idOrSlug)
            ?: animeGateway.findById(idOrSlug)
            ?: fallback(idOrSlug)

        val episodes = anime.id.let(releaseGateway::findByAnimeIdOrderByEpisodeDesc).map {
            return@map EpisodeInfo(
                number = it.animeEpisode,
                title = it.animeName,
                options = it.options,
                type = anime.getTypeDescription()
            )
        }

        return anime.copy(episodes = episodes)
    }

    fun fallback(idOrSlug: String): Anime {
        val possibleName = idOrSlug.replace("_", " ")
        return jikanGateway.search(possibleName).let(animeGateway::saveAll).first()
    }

}