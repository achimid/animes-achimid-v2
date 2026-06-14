package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.PushSubscription
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PushSubscriptionGateway
import org.springframework.stereotype.Component

@Component
class SavePushSubscriptionUseCase(private val pushSubscriptionGateway: PushSubscriptionGateway) {

    fun execute(userId: String, endpoint: String, p256dh: String, auth: String) {
        pushSubscriptionGateway.save(
            PushSubscription(userId = userId, endpoint = endpoint, p256dh = p256dh, auth = auth)
        )
    }
}
