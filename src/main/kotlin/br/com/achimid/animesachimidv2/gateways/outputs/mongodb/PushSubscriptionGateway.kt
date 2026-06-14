package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.PushSubscription
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.PushSubscriptionDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.PushSubscriptionRepository
import org.springframework.stereotype.Component

@Component
class PushSubscriptionGateway(private val repository: PushSubscriptionRepository) {

    fun save(subscription: PushSubscription) {
        if (repository.existsByUserIdAndEndpoint(subscription.userId, subscription.endpoint)) return
        repository.save(
            PushSubscriptionDocument(
                userId = subscription.userId,
                endpoint = subscription.endpoint,
                p256dh = subscription.p256dh,
                auth = subscription.auth,
            )
        )
    }

    fun findByUserId(userId: String): List<PushSubscription> =
        repository.findByUserId(userId).map { it.toDomain() }

    fun deleteByEndpoint(endpoint: String) = repository.deleteByEndpoint(endpoint)

    private fun PushSubscriptionDocument.toDomain() = PushSubscription(
        id = id,
        userId = userId,
        endpoint = endpoint,
        p256dh = p256dh,
        auth = auth,
    )
}
