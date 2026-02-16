package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import br.com.achimid.animesachimidv2.domains.Anime
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "catalog-names")
data class NameDocument(
    @TextIndexed
    val name: String,
    val animeId: String,
)
