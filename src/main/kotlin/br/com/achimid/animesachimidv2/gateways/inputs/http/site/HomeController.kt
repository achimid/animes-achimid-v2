package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PageAccessGateway
import br.com.achimid.animesachimidv2.usecases.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync


@Controller
@RequestMapping("/")
class HomeController(
    val mockUseCase: MockUseCase,
    val pageAccessGateway: PageAccessGateway,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findReleasesUseCase: FindReleasesUseCase,
    val findTodayCalendarReleaseUseCase: FindTodayCalendarReleaseUseCase
) {

    @GetMapping
    fun homePage(model: Model): String {

        val releases = supplyAsync {findReleasesUseCase.execute(0, 20)}
        val recommendations = supplyAsync {findRecommendationsUseCase.execute()}
        val calendarRelease = supplyAsync {findTodayCalendarReleaseUseCase.execute()}
        val fallowingList = supplyAsync {mockUseCase.getFallowing()}
        val siteIntegrations = supplyAsync {findSiteIntegrationsUseCase.execute()}
        val pageAccess = supplyAsync { pageAccessGateway.getPageAccess() }

        allOf(releases, recommendations, calendarRelease, fallowingList, siteIntegrations, pageAccess).join()

        model.addAttribute("releases", releases.join())
        model.addAttribute("releasesEpisodes", releases.join().toList().slice(0..5))
        model.addAttribute("recommendations", recommendations.join())
        model.addAttribute("calendarRelease", calendarRelease.join())
        model.addAttribute("fallowingList", fallowingList.join())
        model.addAttribute("siteIntegrations", siteIntegrations.join())
        model.addAttribute("pageAccess", pageAccess.join())

        return "home"
    }

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}