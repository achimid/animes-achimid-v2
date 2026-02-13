package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.stereotype.Component

@Component
class AddCommentUserCase(
    private val animeGateway: AnimeGateway
) {

    fun execute(animeId: String, comment: AnimeComment) = animeGateway.addComment(animeId, comment)

}