package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NotificationDocument
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Mapper manual para Notification ↔ NotificationDocument (FUNC-07).
 * Define `createdAt` explicitamente porque o auditing do Mongo não está habilitado no projeto.
 */
@Component
class NotificationDocumentMapper {

    fun fromDocument(document: NotificationDocument): Notification = Notification(
        id = document.id,
        userId = document.userId,
        animeId = document.animeId,
        animeName = document.animeName,
        animeSlug = document.animeSlug,
        animeImageUrl = document.animeImageUrl,
        episode = document.episode,
        read = document.read,
        createdAt = document.createdAt ?: Instant.now(),
    )

    fun toDocument(domain: Notification): NotificationDocument = NotificationDocument(
        id = domain.id,
        userId = domain.userId,
        animeId = domain.animeId,
        animeName = domain.animeName,
        animeSlug = domain.animeSlug,
        animeImageUrl = domain.animeImageUrl,
        episode = domain.episode,
        read = domain.read,
        createdAt = domain.createdAt,
    )
}
