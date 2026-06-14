package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.PushSubscriptionDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface PushSubscriptionRepository : MongoRepository<PushSubscriptionDocument, String> {
    fun findByUserId(userId: String): List<PushSubscriptionDocument>
    fun deleteByEndpoint(endpoint: String)
    fun existsByUserIdAndEndpoint(userId: String, endpoint: String): Boolean
}
