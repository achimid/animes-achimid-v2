package br.com.achimid.animesachimidv2.gateways.inputs.http.api.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CallbackIntegrationRequest(
    val executionRequest: ExecutionRequest? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionRequest(
    val uuid: String,
    val isSuccess: Boolean,
    val startTime: String,
    val endTime: String,
    val executionTime: String,
    val error: Any? = null,
    val result: List<ExecutionResultRequest>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionResultRequest(
    val from: String? = null,
    val url: String? = null,
    val title: String? = null,
    val anime: String? = null,
    val episode: Int? = null,
    val data: Map<String, List<ExecutionResultMirrorRequest>>? = null,
) {
    fun getMirrors() : List<ExecutionResultMirrorRequest>? = data?.get("mirrors")
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionResultMirrorRequest(
    val description: String? = null,
    val url: String? = null,
)