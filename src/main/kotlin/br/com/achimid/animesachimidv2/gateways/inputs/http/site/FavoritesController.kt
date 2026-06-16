package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping

@Controller
class FavoritesController(
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase,
    val findUserUseCase: FindUserUseCase,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
) {

    @GetMapping("/favorites")
    fun favorites(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        val user = userId?.let { findUserUseCase.execute(it) }
        model.addAttribute("favorites", findFavoriteAnimesUseCase.execute(userId))
        model.addAttribute("siteIntegrations", findSiteIntegrationsUseCase.execute())
        model.addAttribute("notifPrefs", user?.notificationSitePreferences ?: emptyMap<String, Set<String>>())
        return "favorites"
    }
}
