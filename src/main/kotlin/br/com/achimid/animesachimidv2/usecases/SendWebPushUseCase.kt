package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.gateways.outputs.http.webpush.WebPushGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PushSubscriptionGateway
import org.springframework.stereotype.Component

@Component
class SendWebPushUseCase(
    private val pushSubscriptionGateway: PushSubscriptionGateway,
    private val webPushGateway: WebPushGateway,
) {

    fun execute(userId: String, notification: Notification) {
        val subscriptions = pushSubscriptionGateway.findByUserId(userId)
        if (subscriptions.isEmpty()) return

        // Build payload manually to avoid Jackson 3 import complexity
        val payload = buildString {
            append("""{"title":""")
            append('"'); append(escape(notification.animeName)); append('"')
            append(""","body":"Episódio ${escape(notification.episode)} disponível!"""")
            append(""","slug":"""")
            append(escape(notification.animeSlug))
            append('"')
            if (notification.animeImageUrl != null) {
                append(""","icon":"""")
                append(escape(notification.animeImageUrl))
                append('"')
            }
            append('}')
        }

        subscriptions.forEach { sub -> webPushGateway.send(sub, payload) }
    }

    private fun escape(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
}
