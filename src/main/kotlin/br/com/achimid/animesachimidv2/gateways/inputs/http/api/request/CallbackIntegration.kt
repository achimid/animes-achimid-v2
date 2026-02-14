package br.com.achimid.animesachimidv2.gateways.inputs.http.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegration(
    val request: RequestCallbackIntegration,
    val execution: CallbackIntegrationExecution? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestCallbackIntegration(
    val ref: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegrationExecution(
    val result: List<CallbackIntegrationExecutionResult>? = null,
    val error: Any? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegrationExecutionResult(
    val from: String,
    val url: String,
    val title: String,
    val anime: String? = null,
    val episode: String? = null,
    val languages: List<String>? = null,
    val isDub: Boolean? = null,
    val data: CallbackIntegrationExecutionDataWrapper? = null
) {
    fun getIdt(): String {
        return (title + anime + episode + from + url).replace("\\s".toRegex(), "")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegrationExecutionDataWrapper(
    val mirrors: List<CallbackIntegrationExecutionMirror>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegrationExecutionMirror(
    val description: String,
    val url: String
)