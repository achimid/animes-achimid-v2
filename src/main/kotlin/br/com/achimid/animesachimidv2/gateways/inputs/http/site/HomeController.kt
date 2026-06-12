package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PageAccessGateway
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
    val findReleasesUseCase: FindReleasesUseCase,
    val findFallowingAnimesUseCase: FindFallowingAnimesUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val findTodayCalendarReleaseUseCase: FindTodayCalendarReleaseUseCase,
    val findUserUseCase: FindUserUseCase
) {

    @GetMapping
    fun homePage(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {

        val releases = supplyAsync { findReleasesUseCase.execute(0, 20) }
        val recommendations = supplyAsync { findRecommendationsUseCase.execute() }
        val calendarRelease = supplyAsync { findTodayCalendarReleaseUseCase.execute() }
        val fallowingList = supplyAsync { findFallowingAnimesUseCase.execute(userId) }
        val siteIntegrations = supplyAsync { findSiteIntegrationsUseCase.execute() }
        val pageAccess = supplyAsync { pageAccessGateway.getPageAccess() }
        val user = supplyAsync { if (userId != null) findUserUseCase.execute(userId) else null }

        allOf(releases, recommendations, calendarRelease, fallowingList, siteIntegrations, pageAccess, user).join()

        model.addAttribute("releases", releases.join())
        model.addAttribute("releasesEpisodes", releases.join().toList().take(5))
        model.addAttribute("recommendations", recommendations.join())
        model.addAttribute("calendarRelease", calendarRelease.join())
        model.addAttribute("fallowingList", fallowingList.join())
        model.addAttribute("siteIntegrations", siteIntegrations.join())
        model.addAttribute("pageAccess", pageAccess.join())
        model.addAttribute("isAdmin", user.join()?.isAdmin ?: false)

        return "home"
    }

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}