package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PageAccessGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import br.com.achimid.animesachimidv2.usecases.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync


@Controller
@RequestMapping("/")
class HomeController(
    val pageAccessGateway: PageAccessGateway,
    val animeGateway: AnimeGateway,
    val releaseGateway: ReleaseGateway,
    val findReleasesUseCase: FindReleasesUseCase,
    val findFallowingAnimesUseCase: FindFallowingAnimesUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val findTodayCalendarReleaseUseCase: FindTodayCalendarReleaseUseCase,
    val findUserUseCase: FindUserUseCase,
    val findFeaturedAnimeUseCase: FindFeaturedAnimeUseCase,
) {

    @GetMapping
    fun homePage(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {

        val releases = supplyAsync { findReleasesUseCase.execute(0, 20) }
        val recommendations = supplyAsync { findRecommendationsUseCase.execute(userId = userId) }
        val calendarRelease = supplyAsync { findTodayCalendarReleaseUseCase.execute() }
        val fallowingList = supplyAsync { findFallowingAnimesUseCase.execute(userId) }
        val siteIntegrations = supplyAsync { findSiteIntegrationsUseCase.execute() }
        val pageAccess = supplyAsync { pageAccessGateway.getPageAccess() }
        val user = supplyAsync { if (userId != null) findUserUseCase.execute(userId) else null }
        val statsAnimes = supplyAsync { animeGateway.count() }
        val statsReleasesToday = supplyAsync { releaseGateway.countToday() }
        val featuredAnime = supplyAsync { findFeaturedAnimeUseCase.execute() }

        allOf(releases, recommendations, calendarRelease, fallowingList, siteIntegrations,
              pageAccess, user, statsAnimes, statsReleasesToday, featuredAnime).join()

        val siteIntegrationsList = siteIntegrations.join()

        model.addAttribute("releases", releases.join())
        model.addAttribute("releasesEpisodes", releases.join().toList().take(5))
        model.addAttribute("recommendations", recommendations.join())
        model.addAttribute("calendarRelease", calendarRelease.join())
        model.addAttribute("fallowingList", fallowingList.join())
        model.addAttribute("siteIntegrations", siteIntegrationsList)
        model.addAttribute("pageAccess", pageAccess.join())
        model.addAttribute("isAdmin", user.join()?.isAdmin ?: false)
        model.addAttribute("statsAnimes", statsAnimes.join())
        model.addAttribute("statsReleasesToday", statsReleasesToday.join())
        model.addAttribute("statsSites", siteIntegrationsList.count { it.enabled })
        model.addAttribute("featuredAnime", featuredAnime.join())

        return "home"
    }

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}