package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.springframework.stereotype.Component

@Component
class UpdateNotificationSitePreferenceUseCase(private val userGateway: UserGateway) {

    fun execute(userId: String, animeId: String, sites: Set<String>?): Boolean {
        userGateway.findById(userId) ?: return false
        userGateway.updateNotificationSitePreference(userId, animeId, sites)
        return true
    }
}
