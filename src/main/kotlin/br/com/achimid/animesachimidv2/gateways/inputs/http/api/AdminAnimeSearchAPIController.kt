package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/animes")
class AdminAnimeSearchAPIController(
    val jikanAPIGateway: JikanAPIGateway,
    val animeGateway: AnimeGateway,
    val adminAccessChecker: AdminAccessChecker,
) {

    @GetMapping("/search-jikan")
    @ResponseStatus(OK)
    fun searchJikan(
        @RequestParam query: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ): List<Anime> {
        adminAccessChecker.requireAdmin(userId)
        val results = jikanAPIGateway.search(query)
        return animeGateway.saveAll(results)
    }
}
