package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.NotificationGateway
import br.com.achimid.animesachimidv2.usecases.FindUserNotificationsUseCase
import br.com.achimid.animesachimidv2.usecases.MarkNotificationsReadUseCase
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Central de notificações in-app (FUNC-07 — fase 1). Tudo é escopado pelo cookie `user_id`,
 * então funciona tanto para usuários autenticados quanto para convidados.
 */
@RestController
@RequestMapping("/api/v1/user/notifications")
class NotificationAPIController(
    val findUserNotificationsUseCase: FindUserNotificationsUseCase,
    val markNotificationsReadUseCase: MarkNotificationsReadUseCase,
    val notificationGateway: NotificationGateway,
) {

    @GetMapping
    @ResponseStatus(OK)
    fun list(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): List<Notification> = findUserNotificationsUseCase.execute(userId)

    @GetMapping("/count")
    @ResponseStatus(OK)
    fun count(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): Map<String, Long> = mapOf("unread" to notificationGateway.countUnread(userId))

    @PostMapping("/read")
    @ResponseStatus(NO_CONTENT)
    fun markRead(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ) = markNotificationsReadUseCase.execute(userId)
}
