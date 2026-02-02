package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.MockUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync


@Controller
@RequestMapping("/anime")
class AnimeController(
    val mockUseCase: MockUseCase
) {

    @GetMapping
    fun animePage(model: Model): String {

        val animeSupply = supplyAsync({ mockUseCase.getAnime() })
        val releasesSupply = supplyAsync({ mockUseCase.getLastReleases() })
        val animeCommentSupply = supplyAsync({ mockUseCase.getAnimeComment() })
        val recommendationsSupply = supplyAsync({ mockUseCase.getRecommendations() })

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