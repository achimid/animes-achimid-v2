package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.NotificationGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Notifica os usuários que favoritaram um anime quando ele ganha um episódio novo (FUNC-07, fase 1).
 * Deduplica por (usuário, anime, episódio) para não notificar de novo a cada mirror do mesmo episódio.
 */
@Component
class NotifyFavoritesUseCase(
    private val userGateway: UserGateway,
    private val notificationGateway: NotificationGateway,
    private val sendWebPushUseCase: SendWebPushUseCase,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(release: Release, anime: Anime, fromSite: String? = null) {
        val episode = release.animeEpisode ?: return

        val users = userGateway.findByFavorite(anime.id)
        if (users.isEmpty()) return

        val notified = users.count { user ->
            val userPrefs = user.notificationSitePreferences?.get(anime.id)
            if (!userPrefs.isNullOrEmpty() && fromSite != null && fromSite !in userPrefs) return@count false

            if (notificationGateway.exists(user.id, anime.id, episode)) return@count false

            val notification = Notification(
                userId = user.id,
                animeId = anime.id,
                animeName = anime.name,
                animeSlug = anime.slug,
                animeImageUrl = anime.imageUrl,
                episode = episode,
            )
            notificationGateway.save(notification)
            sendWebPushUseCase.execute(user.id, notification)
            true
        }

        if (notified > 0) {
            logger.info("Notificações criadas: $notified para '${anime.name}' episódio $episode")
        }
    }
}
