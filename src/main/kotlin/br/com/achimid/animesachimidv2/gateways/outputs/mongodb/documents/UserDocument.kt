package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val email: String? = null,
    val username: String? = null,

    val favorites: List<String>? = null,

    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null
)
