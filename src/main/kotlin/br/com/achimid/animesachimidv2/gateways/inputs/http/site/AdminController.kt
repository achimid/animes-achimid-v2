package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.ModerateCommentUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(
    val adminAccessChecker: AdminAccessChecker,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val moderateCommentUseCase: ModerateCommentUseCase,
    val releaseGateway: ReleaseGateway,
    val animeGateway: AnimeGateway,
) {

    @GetMapping
    fun dashboard(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        if (!adminAccessChecker.isAdmin(userId)) return "redirect:/"

        model.addAttribute("siteIntegrations", findSiteIntegrationsUseCase.execute())
        model.addAttribute("pendingComments", moderateCommentUseCase.listPending())
        model.addAttribute("reviewCount", releaseGateway.findNeedingReview(PageRequest.of(0, 1)).totalElements)
        model.addAttribute("recentAnimes", animeGateway.findRecentlyAdded(30))
        return "admin"
    }

    @GetMapping("/releases/review")
    fun releaseReview(
        @RequestParam(required = false) pageNumber: Int = 0,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        if (!adminAccessChecker.isAdmin(userId)) return "redirect:/"

        val page = releaseGateway.findNeedingReview(PageRequest.of(pageNumber, 30))
        model.addAttribute("releases", page.content)
        model.addAttribute("currentPage", pageNumber)
        model.addAttribute("totalPages", page.totalPages)
        model.addAttribute("totalElements", page.totalElements)
        return "admin-review"
    }
}
