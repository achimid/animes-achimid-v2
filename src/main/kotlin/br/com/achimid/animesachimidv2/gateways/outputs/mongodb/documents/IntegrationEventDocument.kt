package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "integration-events")
data class IntegrationEventDocument(
    @Id
    val id: String? = null,
//    @Indexed(unique = true)
//    @Indexed
    val idt: String,
    val from: String,
    val url: String,
    val title: String,
    val anime: String? = null,
    val episode: String? = null,
    val data: MirrorDataDocument? = null,
    @CreatedDate
    @Indexed(expireAfter = "3d")
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)

data class MirrorDataDocument(
    val mirrors: List<MirrorDocument>? = null
)

data class MirrorDocument(
    val description: String,
    val url: String
)