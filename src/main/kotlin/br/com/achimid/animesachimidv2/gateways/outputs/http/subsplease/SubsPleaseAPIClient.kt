package br.com.achimid.animesachimidv2.gateways.outputs.http.subsplease

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "subsplease", url = "\${external.subsplease.url}")
interface SubsPleaseAPIClient {

    @GetMapping("/", consumes = [TEXT_HTML_VALUE])
    fun findFullSchedule(
        @RequestParam("f") f: String = "schedule",
        @RequestParam("tz") tz: String = "America/Sao_Paulo",
    ): ResponseEntity<String>

}