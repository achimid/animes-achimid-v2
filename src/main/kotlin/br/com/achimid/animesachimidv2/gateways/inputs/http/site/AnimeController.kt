package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindAnimesUseCase
import br.com.achimid.animesachimidv2.usecases.FindRecommendationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.GetAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.MockUseCase
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
    val getAnimeUseCase: GetAnimeUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findReleasesUseCase: FindReleasesUseCase
) {

    @GetMapping("/{idOrSlug}")
    fun animePage(model: Model, @PathVariable idOrSlug: String): String {

        val animeSupply = supplyAsync({ getAnimeUseCase.execute(idOrSlug) })
        val releasesSupply = supplyAsync({ findReleasesUseCase.execute(0, 6) })
        val animeCommentSupply = supplyAsync({ mockUseCase.getAnimeComment() })
        val recommendationsSupply = supplyAsync({ findRecommendationsUseCase.execute(3) })

        allOf(
            animeSupply,
            releasesSupply,
            animeCommentSupply,
            recommendationsSupply
        ).join()

        val anime = animeSupply.join()
        val releases = releasesSupply.join()
        val animeComment = animeCommentSupply.join()
        val recommendations = recommendationsSupply.join()

        model.addAttribute("anime", anime)
        model.addAttribute("releases", releases)
        model.addAttribute("animeComment", animeComment)
        model.addAttribute("recommendations", recommendations)

        return "anime"
    }

}