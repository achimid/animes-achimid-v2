package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.CommentStatus
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class AddCommentUserCase(
    private val animeGateway: AnimeGateway
) {

    // Todo novo comentário entra como PENDING e passa pela moderação (FUNC-05).
    @CacheEvict("animeCache")
    fun execute(animeId: String, comment: AnimeComment) =
        animeGateway.addComment(animeId, comment.copy(content = comment.content.trim(), status = CommentStatus.PENDING))

}