package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.NotificationGateway
import org.springframework.stereotype.Component

/**
 * Marca como lidas todas as notificações de um usuário (FUNC-07), ao abrir a central no header.
 */
@Component
class MarkNotificationsReadUseCase(
    private val notificationGateway: NotificationGateway,
) {

    fun execute(userId: String?) {
        if (userId.isNullOrEmpty()) return
        notificationGateway.markAllRead(userId)
    }
}
