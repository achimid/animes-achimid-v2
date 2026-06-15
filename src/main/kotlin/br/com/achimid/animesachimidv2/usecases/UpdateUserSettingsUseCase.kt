package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.stereotype.Component

@Component
class UpdateUserSettingsUseCase(val userGateway: UserGateway) {

    fun execute(userId: String, username: String?): Boolean {
        val user = userGateway.findById(userId) ?: return false
        userGateway.save(user.copy(username = username?.trim()?.takeIf { it.isNotEmpty() }))
        return true
    }
}
