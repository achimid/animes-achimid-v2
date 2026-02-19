package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "catalog-names")
data class NameDocument(
    @TextIndexed
    val name: String,
    val animeId: String,

    @Id
    val id: String = name + animeId,
    val potential: Boolean? = null,
    val animeName: String? = null,
)
