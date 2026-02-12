package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

data class SiteIntegrationDocument(
    val name: String,
    val url: String,
    val script: String? = null,
    val enabled: Boolean = true,
    val lastExecutionDate: String? = null,
    val lastExecutionSuccess: Boolean = false
)
