package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * API do usuário (FUNC-02). Por enquanto expõe a lista completa de favoritos do usuário
 * identificado pelo cookie `user_id`.
 */
@RestController
@RequestMapping("/api/v1/user")
class UserAPIController(
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase
) {

    @GetMapping("/favorites")
    @ResponseStatus(OK)
    fun favorites(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): List<Anime> = findFavoriteAnimesUseCase.execute(userId)
}
