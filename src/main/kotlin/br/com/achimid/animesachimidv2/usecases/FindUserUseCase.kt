package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.stereotype.Component

@Component
class FindUserUseCase(
    private val userGateway: UserGateway
) {

    fun execute(id: String): User? = userGateway.findById(id)
}
