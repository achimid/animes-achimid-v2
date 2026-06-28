package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.NotificationGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import kotlin.math.floor

/**
 * Notifica os usuários que favoritaram um anime quando ele ganha um episódio novo (FUNC-07, fase 1).
 *
 * Proteção contra flood em dois níveis:
 * 1. Cooldown de 30 min por (userId, animeId) — impede notificações repetidas do mesmo anime
 *    quando múltiplos sites reportam o mesmo episódio em sequência.
 * 2. Deduplicação permanente por (userId, animeId, episódio normalizado) no MongoDB.
 */
@Component
class NotifyFavoritesUseCase(
    private val userGateway: UserGateway,
    private val notificationGateway: NotificationGateway,
    private val sendWebPushUseCase: SendWebPushUseCase,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val cooldown: Cache<String, Boolean> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()

    fun execute(release: Release, anime: Anime, fromSite: String? = null) {
        val episode = release.animeEpisode?.normalizeEpisode() ?: return

        val users = userGateway.findByFavorite(anime.id)
        if (users.isEmpty()) return

        val notified = users.count { user ->
            val cooldownKey = "${user.id}:${anime.id}"

            // Nível 1: cooldown em memória (30 min)
            if (cooldown.getIfPresent(cooldownKey) != null) return@count false

            // Filtro de preferência de site
            val userPrefs = user.notificationSitePreferences?.get(anime.id)
            if (!userPrefs.isNullOrEmpty() && fromSite != null && fromSite !in userPrefs) return@count false

            // Nível 2: dedup permanente por episódio normalizado
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
            cooldown.put(cooldownKey, true)
            true
        }

        if (notified > 0) {
            logger.info("Notificações criadas: $notified para '${anime.name}' episódio $episode")
        }
    }

    /**
     * Normaliza o número do episódio para evitar que variações do mesmo número
     * (ex.: "12", "12.0", "012") criem duplicatas no dedup permanente.
     */
    private fun String.normalizeEpisode(): String {
        val num = this.trim().toDoubleOrNull()
            ?: return this.trim()
        return if (num == floor(num)) num.toInt().toString() else num.toString()
    }
}
