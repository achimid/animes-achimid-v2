package br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate

import br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate.request.TranslateRequest
import br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate.request.TranslateResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "libreTranslate", url = "\${external.libre-translate.url}")
interface LibreTranslateAPIClient {

    @PostMapping("/translate", consumes = ["application/json"])
    fun translate(@RequestBody request: TranslateRequest): TranslateResponse

}