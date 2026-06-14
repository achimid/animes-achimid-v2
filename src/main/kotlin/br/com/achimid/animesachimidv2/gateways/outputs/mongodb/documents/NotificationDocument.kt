package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "notifications")
data class NotificationDocument(
    @Id
    val id: String? = null,

    @Indexed
    val userId: String,
    val animeId: String,
    val animeName: String,
    val animeSlug: String,
    val animeImageUrl: String? = null,
    val episode: String,
    val read: Boolean = false,

    @CreatedDate
    val createdAt: Instant? = null,
)
