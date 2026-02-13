package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.FindRecommendationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.MockUseCase
import br.com.achimid.animesachimidv2.usecases.RegisterAnimeVisitUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync


@Controller
@RequestMapping("/anime")
class AnimeController(
    val mockUseCase: MockUseCase,
    val findAnimeUseCase: FindAnimeUseCase,
    val findReleasesUseCase: FindReleasesUseCase,
    val registerAnimeVisitUseCase: RegisterAnimeVisitUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase
) {

    @GetMapping("/{idOrSlug}")
    fun animePage(model: Model, @PathVariable idOrSlug: String): String {

        val animeSupply = supplyAsync { findAnimeUseCase.execute(idOrSlug) }
        val releasesSupply = supplyAsync { findReleasesUseCase.execute(0, 5) }
        val recommendationsSupply = supplyAsync { findRecommendationsUseCase.execute(3) }

        allOf(animeSupply, releasesSupply, recommendationsSupply).join()

        val anime = animeSupply.join()

        model.addAttribute("anime", anime)
        model.addAttribute("releases", releasesSupply.join())
        model.addAttribute("recommendations", recommendationsSupply.join())

        registerAnimeVisitUseCase.execute(anime.id)

        return "anime"
    }

}