package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old

import br.com.achimid.animesachimidv2.domains.Jikan
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "animes-old")
data class AnimeOldDocument(
    @Id
    val id: ObjectId? = null,
    val name: String? = null,
    val names: List<String> = listOf(),
    val synonyms: List<String> = listOf(),
    val image: String,
    val type: String? = null,
    val description: String? = null,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val source: SourceWrapper? = null
)

data class SourceWrapper(
    val jikan: Jikan? = null,
)
