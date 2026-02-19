package br.com.achimid.animesachimidv2.domains

import br.com.achimid.animesachimidv2.domains.SiteIntegrationType.SLOW
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofPattern

data class SiteIntegration(
    @JsonIgnore val type: SiteIntegrationType = SLOW,
    val name: String,
    val url: String,
    @JsonIgnore val script: String? = null,
    @JsonIgnore val enabled: Boolean = true,
    @JsonIgnore val skipImage: Boolean = true,
    @JsonIgnore val disableJavaScript: Boolean? = null,
    var lastExecutionDate: Instant? = null,
    var lastExecutionSuccess: Boolean = false
) {
    @JsonProperty("lastExecutionDateFormatted")
    fun lastExecutionDateFormatted(): String? {
        return lastExecutionDate
            ?.atZone(ZoneId.of("America/Sao_Paulo"))
            ?.format(ofPattern("dd/MM/yyyy HH:mm"))
    }
}

enum class SiteIntegrationType { SLOW, MEDIUM, FAST }


data class SiteIntegrationResponse(
    val sites: List<SiteIntegration>
)