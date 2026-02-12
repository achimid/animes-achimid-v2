package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PageAccessGateway
import br.com.achimid.animesachimidv2.usecases.FindRecommendationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.MockUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/")
class HomeController(
    val mockUseCase: MockUseCase,
    val pageAccessGateway: PageAccessGateway,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findReleasesUseCase: FindReleasesUseCase
) {

    @GetMapping
    fun homePage(model: Model): String {

        val releases = findReleasesUseCase.execute(0, 24)
        val recommendations = findRecommendationsUseCase.execute()
        val calendarRelease = mockUseCase.getCalendarRelease()
        val fallowingList = mockUseCase.getFallowing()
        val siteIntegrations = findSiteIntegrationsUseCase.execute()

        model.addAttribute("releases", releases)
        model.addAttribute("releasesEpisodes", releases.toList().slice(0..6))
        model.addAttribute("recommendations", recommendations)
        model.addAttribute("calendarRelease", calendarRelease)
        model.addAttribute("fallowingList", fallowingList)
        model.addAttribute("siteIntegrations", siteIntegrations)
        model.addAttribute("pageAccess", pageAccessGateway.getPageAccess())

        return "home"
    }

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}