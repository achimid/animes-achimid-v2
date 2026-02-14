package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.UserDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserGateway(
    val repository: UserRepository,
    val mapper: UserDocumentMapper,
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    fun save(user: User): User = user
            .also { logger.info("Saving user ${user.id}") }
            .let(mapper::toDocument)
            .let(repository::save)
            .let(mapper::fromDocument)

    fun addFavorite(userId: String, animeId: String) = repository.addFavorite(userId, animeId)
}