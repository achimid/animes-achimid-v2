package br.com.achimid.animesachimidv2.domains

data class SiteStatus(
    val name: String,
    val url: String? = null,
    val time: String,
    val isOk: Boolean = true
)