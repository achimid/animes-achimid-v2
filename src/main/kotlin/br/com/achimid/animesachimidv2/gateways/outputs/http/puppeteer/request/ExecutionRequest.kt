package br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionRequest(
    val id: String? = null,
    val url: String,
    val script: String,
    val callbackUrl: String,
    val ref: String? = null,
    val config: ExecutionConfig? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExecutionConfig(
    val bypassCSP: Boolean? = true,
    val skipImage: Boolean? = true,
    val useRandomAgent: Boolean? = null,
    val urlProxy: String? = null,
    val logConsole: Boolean? = null,
    val defaultNavigationTimeout: Long? = null,
    val addScriptTagUrl: String? = null,
    val waitTime: Long? = null,
)
