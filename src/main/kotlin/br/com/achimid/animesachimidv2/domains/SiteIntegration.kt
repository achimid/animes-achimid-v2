package br.com.achimid.animesachimidv2.domains

import br.com.achimid.animesachimidv2.domains.SiteIntegrationType.SLOW
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Locale.of

data class SiteIntegration(
    val type: SiteIntegrationType = SLOW,
    val name: String,
    val url: String,
    val script: String? = null,
    val enabled: Boolean = true,
    val skipImage: Boolean = true,
    val disableJavaScript: Boolean? = null,
    var lastExecutionDate: Instant? = null,
    var lastExecutionSuccess: Boolean = false
) {
    fun lastExecutionDateFormatted(): String? {
        return lastExecutionDate
            ?.atZone(ZoneId.of("America/Sao_Paulo"))
            ?.format(ofPattern("dd-MM-yyyy HH:mm"))
    }
}

enum class SiteIntegrationType { SLOW, MEDIUM, FAST }


data class SiteIntegrationResponse(
    val sites: List<SiteIntegration>
)