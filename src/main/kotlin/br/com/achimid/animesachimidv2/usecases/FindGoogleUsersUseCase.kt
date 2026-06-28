package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class FindGoogleUsersUseCase(
    private val userGateway: UserGateway
) {
    fun execute(page: Int, size: Int, query: String?): Page<User> =
        userGateway.findGoogleUsers(page, size, query)
}
