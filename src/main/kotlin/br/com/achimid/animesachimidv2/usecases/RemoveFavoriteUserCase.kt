package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class RemoveFavoriteUserCase(
    private val userGateway: UserGateway
) {

    @CacheEvict("fallowingAnimes")
    fun execute(animeId: String, userId: String) = userGateway.removeFavorite(userId, animeId)

}