package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.FindRecommendationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.RegisterAnimeVisitUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.bson.types.ObjectId
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync


@Controller
@RequestMapping("/anime")
class AnimeController(
    val findAnimeUseCase: FindAnimeUseCase,
    val findReleasesUseCase: FindReleasesUseCase,
    val registerAnimeVisitUseCase: RegisterAnimeVisitUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findUserUseCase: FindUserUseCase
) {

    @GetMapping("/{idOrSlug}")
    fun animePage(
        model: Model,
        @PathVariable idOrSlug: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): String {
        if (ObjectId.isValid(idOrSlug)) return "redirect:/"

        val animeSupply = supplyAsync { findAnimeUseCase.execute(idOrSlug) }
        val releasesSupply = supplyAsync { findReleasesUseCase.execute(0, 5) }
        val recommendationsSupply = supplyAsync { findRecommendationsUseCase.execute(3) }
        val userSupply = supplyAsync { if (userId != null) findUserUseCase.execute(userId) else null }

        allOf(animeSupply, releasesSupply, recommendationsSupply, userSupply).join()

        val anime = animeSupply.join()

        model.addAttribute("anime", anime)
        model.addAttribute("releases", releasesSupply.join())
        model.addAttribute("recommendations", recommendationsSupply.join())
        model.addAttribute("isAdmin", userSupply.join()?.isAdmin ?: false)

        registerAnimeVisitUseCase.execute(anime.id)

        return "anime"
    }

}