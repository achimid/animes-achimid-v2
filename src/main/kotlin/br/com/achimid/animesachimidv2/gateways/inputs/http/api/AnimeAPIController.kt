package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import br.com.achimid.animesachimidv2.usecases.AddCommentUserCase
import br.com.achimid.animesachimidv2.usecases.AddFavoriteUserCase
import br.com.achimid.animesachimidv2.usecases.RemoveFavoriteUserCase
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/v1/anime")
class AnimeAPIController(
    val addCommentUserCase: AddCommentUserCase,
    val addFavoriteUserCase: AddFavoriteUserCase,
    val removeFavoriteUserCase: RemoveFavoriteUserCase,
    val userGateway: UserGateway,
) {

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/comment")
    fun addComment(
        @PathVariable id: String,
        @CookieValue(value = "user_id", required = false) userId: String?,
        @RequestBody comment: AnimeComment
    ): AnimeComment {
        val length = comment.content.trim().length
        if (length !in 3..1000) {
            throw ResponseStatusException(BAD_REQUEST, "O comentário deve ter entre 3 e 1000 caracteres")
        }
        val user = userId?.let { userGateway.findById(it) }
        val resolvedName = user?.username ?: user?.email?.substringBefore("@") ?: comment.userName
        val enriched = comment.copy(
            userId = userId ?: comment.userId,
            userName = resolvedName,
            avatar = resolvedName?.take(2)?.uppercase() ?: "AA",
        )
        return addCommentUserCase.execute(id, enriched)
    }

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/favorite")
    fun addFavorite(
        @PathVariable id: String,
        @CookieValue(value = "user_id") userId: String
    ) {
        return addFavoriteUserCase.execute(id, userId)
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}/favorite")
    fun removeFavorite(
        @PathVariable id: String,
        @CookieValue(value = "user_id") userId: String
    ) {
        return removeFavoriteUserCase.execute(id, userId)
    }

}