package br.com.achimid.animesachimidv2.domains

import java.time.Instant

/**
 * Notificação in-app de um novo episódio de um anime favoritado (FUNC-07 — fase 1).
 * É criada quando um [Release] surge para um anime que o usuário marcou como favorito.
 */
data class Notification(
    val id: String? = null,
    val userId: String,
    val animeId: String,
    val animeName: String,
    val animeSlug: String,
    val animeImageUrl: String? = null,
    val episode: String,
    val read: Boolean = false,
    val createdAt: Instant = Instant.now(),
)
