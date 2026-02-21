package br.com.achimid.animesachimidv2.gateways.outputs.http.jikan

import br.com.achimid.animesachimidv2.domains.JikanApiResponse
import br.com.achimid.animesachimidv2.domains.JikanApiSingleResponse
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "jikan", url = "\${external.jikan.url}")
interface JikanAPIClient {

    @Cacheable("jikanIntegrationCache")
    @GetMapping("/v4/anime")
    fun searchAnime(
        @RequestParam("q") query: String,
        @RequestParam("limit") limit: Int = 20,
        @RequestParam("page") page: Int = 1,
    ): JikanApiResponse

    @Cacheable("jikanIntegrationCache")
    @GetMapping("/v4/anime/{id}/full")
    fun findById(@PathVariable id: String): JikanApiSingleResponse

}