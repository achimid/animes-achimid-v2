package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.stereotype.Component

@Component
class FindFeaturedAnimeUseCase(private val animeGateway: AnimeGateway) {
    fun execute(): Anime? = animeGateway.findFeatured()
}
