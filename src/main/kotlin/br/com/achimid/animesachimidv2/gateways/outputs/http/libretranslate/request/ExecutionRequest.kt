package br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class TranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String = "pt-br",
    val format: String = "text"
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TranslateResponse(
    val translatedText: String
)