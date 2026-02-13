package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Recommendation
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class FindRecommendationsUseCase(
    private val animeGateway: AnimeGateway
) {

    fun execute(size: Int = 5) : Page<Recommendation> = animeGateway.findRandom(size).map(this::mapper)
    
    fun mapper(anime: Anime): Recommendation {
        return Recommendation(
            id = anime.id,
            slug = anime.slug,
            title = anime.name,
            imageUrl = anime.imageUrl,
            meta = anime.tags?.joinToString("/") ?: "",
            score = anime.score ?: 0.0
        )
    }

}