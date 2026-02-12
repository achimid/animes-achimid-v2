package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "integration-events")
data class IntegrationEventDocument(
    @Id
    val id: ObjectId? = null,
    @Indexed(unique = true)
    val idt: String,
    val from: String,
    val url: String,
    val title: String,
    val anime: String,
    val episode: String,
    val data: MirrorDataDocument? = null,
    @CreatedDate
    @Indexed(expireAfter = "3d")
    val createdAt: Instant,
    @LastModifiedDate
    val updatedAt: Instant,
)

data class MirrorDataDocument(
    val mirrors: List<MirrorDocument>
)

data class MirrorDocument(
    val description: String,
    val url: String
)