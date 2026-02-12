package br.com.achimid.animesachimidv2.domains

data class SiteIntegration(
    val name: String,
    val url: String,
    val script: String? = null,
    val enabled: Boolean = true,
    val skipImage: Boolean = true,
    val lastExecutionDate: String? = null,
    val lastExecutionSuccess: Boolean = false
)

data class SiteIntegrationResponse(
    val sites: List<SiteIntegration>
)