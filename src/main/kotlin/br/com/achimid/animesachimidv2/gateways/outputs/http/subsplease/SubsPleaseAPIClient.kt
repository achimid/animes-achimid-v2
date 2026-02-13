package br.com.achimid.animesachimidv2.gateways.outputs.http.subsplease

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "subsplease", url = "\${external.subsplease.url}")
interface SubsPleaseAPIClient {

    @GetMapping("/?f=schedule&tz=America/Sao_Paulo", consumes = [TEXT_HTML_VALUE])
    fun findFullSchedule(): ResponseEntity<String>

}