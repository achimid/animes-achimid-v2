package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.CommentStatus
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

/** Comentário pendente com o contexto do anime, para a tela de moderação (FUNC-05). */
data class PendingComment(
    val animeId: String,
    val animeSlug: String,
    val animeName: String,
    val comment: AnimeComment,
)

/** Comentário com contexto do anime, para a listagem completa no painel admin. */
data class CommentWithContext(
    val animeId: String,
    val animeSlug: String,
    val animeName: String,
    val comment: AnimeComment,
)

/**
 * Moderação de comentários (FUNC-05): lista pendentes/todos, aprova/rejeita/exclui.
 * Qualquer mutação invalida o cache do anime para refletir a mudança na página de detalhe.
 */
@Component
class ModerateCommentUseCase(
    private val animeGateway: AnimeGateway
) {

    fun listPending(): List<PendingComment> =
        animeGateway.findAnimesWithPendingComments().flatMap { anime ->
            anime.comments.orEmpty()
                .filter { it.status == CommentStatus.PENDING }
                .map { PendingComment(anime.id, anime.slug, anime.name, it) }
        }.sortedByDescending { it.comment.createdAt }

    fun listAll(): List<CommentWithContext> =
        animeGateway.findAnimesWithAnyComments().flatMap { anime ->
            anime.comments.orEmpty()
                .map { CommentWithContext(anime.id, anime.slug, anime.name, it) }
        }.sortedByDescending { it.comment.createdAt }

    @CacheEvict("animeCache", key = "#animeId")
    fun moderate(animeId: String, commentId: String, approve: Boolean): Boolean =
        animeGateway.updateCommentStatus(
            animeId,
            commentId,
            if (approve) CommentStatus.APPROVED else CommentStatus.REJECTED
        )

    @CacheEvict("animeCache", key = "#animeId")
    fun delete(animeId: String, commentId: String): Boolean =
        animeGateway.deleteComment(animeId, commentId)
}
