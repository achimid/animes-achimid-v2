package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserNotificationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/user")
class UserController(
    val findUserUseCase: FindUserUseCase,
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase,
    val findUserNotificationsUseCase: FindUserNotificationsUseCase,
    val findSiteIntegrationsUseCase: FindSiteIntegrationsUseCase,
) {

    @GetMapping
    fun profile(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        val user = userId?.let { findUserUseCase.execute(it) }
        val favorites = findFavoriteAnimesUseCase.execute(userId)
        val notifications = findUserNotificationsUseCase.execute(userId, limit = 50)

        val topGenres = favorites
            .flatMap { it.tags.orEmpty() }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        val siteIntegrations = findSiteIntegrationsUseCase.execute()

        model.addAttribute("profileUser", user)
        model.addAttribute("favorites", favorites)
        model.addAttribute("notifications", notifications)
        model.addAttribute("notificationCount", notifications.size)
        model.addAttribute("topGenres", topGenres)
        model.addAttribute("siteIntegrations", siteIntegrations)

        return "user"
    }
}
