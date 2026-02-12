package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


@Document(collection = "page-access")
data class PageAccessDocument(
    @Id
    val id: Int = 1,
    val totalCount: Long? = 13922,
)
