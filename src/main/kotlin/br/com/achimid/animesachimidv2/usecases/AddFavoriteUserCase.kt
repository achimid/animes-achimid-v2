package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class AddFavoriteUserCase(
    private val userGateway: UserGateway
) {

    @CacheEvict("fallowingAnimes", key = "#userId")
    fun execute(animeId: String, userId: String) = userGateway.addFavorite(userId, animeId)

}