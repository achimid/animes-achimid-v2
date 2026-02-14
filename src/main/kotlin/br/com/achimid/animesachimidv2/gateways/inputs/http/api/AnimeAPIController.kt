package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.usecases.AddCommentUserCase
import br.com.achimid.animesachimidv2.usecases.AddFavoriteUserCase
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/anime")
class AnimeAPIController(
    val addCommentUserCase: AddCommentUserCase,
    val addFavoriteUserCase: AddFavoriteUserCase
) {

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/comment")
    fun addComment(
        @PathVariable id: String,
        @RequestBody comment: AnimeComment
    ): AnimeComment {
        return addCommentUserCase.execute(id, comment)
    }

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/favorite")
    fun addFavorite(
        @PathVariable id: String,
        @CookieValue(value = "user_id") userId: String
    ) {
        return addFavoriteUserCase.execute(id, userId)
    }

}