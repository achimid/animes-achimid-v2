package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping

/**
 * Página dedicada de favoritos (FUNC-02), acessível pelo menu. Mostra todos os animes
 * favoritados pelo usuário (cookie `user_id`).
 */
@Controller
class FavoritesController(
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase
) {

    @GetMapping("/favoritos")
    fun favorites(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        model.addAttribute("favorites", findFavoriteAnimesUseCase.execute(userId))
        return "favoritos"
    }
}
