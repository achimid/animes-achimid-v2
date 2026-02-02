package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.MockUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/")
class HomeController(
    val mockUseCase: MockUseCase
) {

    @GetMapping
    fun homePage(model: Model): String {

        val releases = mockUseCase.getLastReleases()
        val recommendations = mockUseCase.getRecommendations()
        val calendarRelease = mockUseCase.getCalendarRelease()
        val fallowingList = mockUseCase.getFallowing()
        val sitesStatus = mockUseCase.getSitesMonitored()

        model.addAttribute("releases", releases)
        model.addAttribute("recommendations", recommendations)
        model.addAttribute("calendarRelease", calendarRelease)
        model.addAttribute("fallowingList", fallowingList)
        model.addAttribute("sitesStatus", sitesStatus)

        return "home"
    }

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}