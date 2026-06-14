package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.NotificationGateway
import org.springframework.stereotype.Component

/**
 * Lista as notificações mais recentes de um usuário (FUNC-07). Identificado pelo cookie `user_id`.
 */
@Component
class FindUserNotificationsUseCase(
    private val notificationGateway: NotificationGateway,
) {

    fun execute(userId: String?, limit: Int = 20): List<Notification> {
        if (userId.isNullOrEmpty()) return emptyList()
        return notificationGateway.findByUser(userId, limit)
    }
}
