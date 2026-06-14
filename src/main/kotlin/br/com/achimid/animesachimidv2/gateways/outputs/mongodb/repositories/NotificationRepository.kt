package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NotificationDocument
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : MongoRepository<NotificationDocument, String> {

    fun findByUserIdOrderByCreatedAtDesc(userId: String, pageRequest: PageRequest): List<NotificationDocument>

    fun existsByUserIdAndAnimeIdAndEpisode(userId: String, animeId: String, episode: String): Boolean

    fun countByUserIdAndReadFalse(userId: String): Long

    /** Marca como lidas todas as notificações não lidas do usuário (updateMulti). */
    @Query("{ 'userId' : ?0, 'read' : false }")
    @Update("{ '\$set' : { 'read' : true } }")
    fun markAllReadByUserId(userId: String)

}
