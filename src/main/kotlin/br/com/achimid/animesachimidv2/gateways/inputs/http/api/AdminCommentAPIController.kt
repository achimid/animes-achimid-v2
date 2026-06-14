package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.usecases.CommentWithContext
import br.com.achimid.animesachimidv2.usecases.ModerateCommentUseCase
import br.com.achimid.animesachimidv2.usecases.PendingComment
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * API de moderação de comentários (FUNC-05), restrita a administradores (cookie `user_id`).
 */
@RestController
@RequestMapping("/api/v1/admin/comments")
class AdminCommentAPIController(
    val moderateCommentUseCase: ModerateCommentUseCase,
    val adminAccessChecker: AdminAccessChecker,
) {

    @GetMapping("/pending")
    fun pending(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): List<PendingComment> {
        adminAccessChecker.requireAdmin(userId)
        return moderateCommentUseCase.listPending()
    }

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/{animeId}/{commentId}/approve")
    fun approve(
        @PathVariable animeId: String,
        @PathVariable commentId: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ) {
        adminAccessChecker.requireAdmin(userId)
        moderateCommentUseCase.moderate(animeId, commentId, approve = true)
    }

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/{animeId}/{commentId}/reject")
    fun reject(
        @PathVariable animeId: String,
        @PathVariable commentId: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ) {
        adminAccessChecker.requireAdmin(userId)
        moderateCommentUseCase.moderate(animeId, commentId, approve = false)
    }

    @GetMapping("/all")
    fun all(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): List<CommentWithContext> {
        adminAccessChecker.requireAdmin(userId)
        return moderateCommentUseCase.listAll()
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{animeId}/{commentId}")
    fun delete(
        @PathVariable animeId: String,
        @PathVariable commentId: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ) {
        adminAccessChecker.requireAdmin(userId)
        moderateCommentUseCase.delete(animeId, commentId)
    }
}
