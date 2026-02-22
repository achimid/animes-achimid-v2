package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindAnimesUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/animes")
class AnimesController(
    val findAnimesUseCase: FindAnimesUseCase
) {

    @GetMapping
    fun catalogPage(model: Model): String {

        val animeList = findAnimesUseCase.execute(0, 50)

        model.addAttribute("animeList", animeList)

        return "animes"
    }

}