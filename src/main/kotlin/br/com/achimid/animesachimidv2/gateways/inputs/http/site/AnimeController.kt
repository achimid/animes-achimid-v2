package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/anime")
class AnimeController {

    @GetMapping("{slug}")
    fun animePage(@PathVariable slug: String): String = "anime"

}