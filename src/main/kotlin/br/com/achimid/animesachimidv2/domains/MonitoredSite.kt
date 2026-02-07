package br.com.achimid.animesachimidv2.domains

data class MonitoredSite(
    val name: String,
    val url: String,
    val script: String? = null,
    val enabled: Boolean = true,
    val lastExecutionDate: String? = null,
    val lastExecutionSuccess: Boolean = false
)

data class MonitoredSites(
    val sites: List<MonitoredSite>
)