package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.stereotype.Component

/**
 * Carrega a lista completa de animes favoritados por um usuário (FUNC-03 — página do usuário).
 * Resolve os IDs guardados em `User.favorites` para os respectivos [Anime].
 */
@Component
class FindFavoriteAnimesUseCase(
    private val userGateway: UserGateway,
    private val animeGateway: AnimeGateway
) {

    fun execute(userId: String?): List<Anime> {
        if (userId.isNullOrEmpty()) return emptyList()

        return userGateway.findById(userId)?.favorites.orEmpty()
            .mapNotNull(animeGateway::findById)
    }
}
