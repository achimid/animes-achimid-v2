package br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer

import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request.ExecutionRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "puppeteer", url = "\${external.puppeteer-executor.url}")
interface PuppeteerAPIClient {

    @PostMapping("/execution")
    fun execute(@RequestBody executionRequest: ExecutionRequest): ResponseEntity<ExecutionRequest>

}