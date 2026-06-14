package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.ModerateCommentUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Área administrativa (FUNC-04): painel de scraping (status/gestão dos sites — via F2) e
 * moderação de comentários (FUNC-05). Acesso restrito a admins (cookie `user_id` → `isAdmin`).
 */
@Controller
@RequestMapping("/admin")
class AdminController(
    val adminAccessChecker: AdminAccessChecker,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
    val moderateCommentUseCase: ModerateCommentUseCase,
) {

    @GetMapping
    fun dashboard(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        if (!adminAccessChecker.isAdmin(userId)) return "redirect:/"

        model.addAttribute("siteIntegrations", findSiteIntegrationsUseCase.execute())
        model.addAttribute("pendingComments", moderateCommentUseCase.listPending())
        return "admin"
    }
}
