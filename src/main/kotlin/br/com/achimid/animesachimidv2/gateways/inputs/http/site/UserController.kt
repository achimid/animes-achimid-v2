package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Página do usuário (FUNC-03): perfil (conta Google ou visitante anônimo) e favoritos.
 * Acessível tanto para autenticados quanto para convidados (cookie `user_id`).
 */
@Controller
@RequestMapping("/usuario")
class UserController(
    val findUserUseCase: FindUserUseCase,
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase
) {

    @GetMapping
    fun profile(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        val user = userId?.let { findUserUseCase.execute(it) }

        model.addAttribute("profileUser", user)
        model.addAttribute("favorites", findFavoriteAnimesUseCase.execute(userId))

        return "usuario"
    }
}
