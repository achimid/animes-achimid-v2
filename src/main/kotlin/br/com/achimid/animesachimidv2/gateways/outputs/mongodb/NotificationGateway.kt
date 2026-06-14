package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.Notification
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.NotificationDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class NotificationGateway(
    val repository: NotificationRepository,
    val mapper: NotificationDocumentMapper,
) {

    fun save(notification: Notification): Notification =
        notification.let(mapper::toDocument).let(repository::save).let(mapper::fromDocument)

    fun findByUser(userId: String, limit: Int): List<Notification> =
        repository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit)).map(mapper::fromDocument)

    fun exists(userId: String, animeId: String, episode: String): Boolean =
        repository.existsByUserIdAndAnimeIdAndEpisode(userId, animeId, episode)

    fun countUnread(userId: String?): Long {
        if (userId.isNullOrBlank()) return 0L
        return repository.countByUserIdAndReadFalse(userId)
    }

    fun markAllRead(userId: String) = repository.markAllReadByUserId(userId)
}
