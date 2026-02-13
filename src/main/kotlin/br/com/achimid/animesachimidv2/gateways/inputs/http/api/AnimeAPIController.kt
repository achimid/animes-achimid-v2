package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.usecases.AddCommentUserCase
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/anime")
class AnimeAPIController(
    val addCommentUserCase: AddCommentUserCase
) {

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/comment")
    fun releases(
        @PathVariable id: String,
        @RequestBody comment: AnimeComment
    ): AnimeComment {
        return addCommentUserCase.execute(id, comment)
    }

}