package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.dto.Fallowing
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class FindFallowingAnimesUseCase(
    private val userGateway: UserGateway,
    private val animeGateway: AnimeGateway
) {

    @Cacheable("fallowingAnimes")
    fun execute(userId: String? = null): List<Fallowing> {
        if (userId.isNullOrEmpty()) return emptyList()

        val ids = userGateway.findById(userId)?.favorites?.take(3).orEmpty()
        return animeGateway.findAllByIds(ids).map { Fallowing(it.name, it.imageUrl) }
    }

}