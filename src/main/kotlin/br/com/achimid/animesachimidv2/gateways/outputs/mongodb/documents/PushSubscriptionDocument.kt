package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "push_subscriptions")
data class PushSubscriptionDocument(
    @Id val id: String? = null,
    @Indexed val userId: String,
    @Indexed(unique = true) val endpoint: String,
    val p256dh: String,
    val auth: String,
    @CreatedDate val createdAt: Instant? = null,
)
