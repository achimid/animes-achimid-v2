package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.UserDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<UserDocument, String> {

    @Query("{ 'id' : ?0 }")
    @Update("{ '\$push' : { 'favorites' : ?1 } }")
    fun addFavorite(userId: String, animeId: String)

    @Query("{ '_id' : ?0 }")
    @Update("{ '\$pull' : { 'favorites' : ?1 } }")
    fun removeFavorite(userId: String, animeId: String)

    fun findByEmail(email: String): UserDocument?

    /** Usuários que favoritaram um anime — base das notificações (FUNC-07). */
    fun findByFavoritesContaining(animeId: String): List<UserDocument>

    /** Todos os usuários com e-mail (login Google). */
    fun findByEmailNotNull(pageable: Pageable): Page<UserDocument>

    /** Busca por e-mail ou username (case-insensitive). */
    @Query("{ '\$or': [ { 'email': { '\$regex': ?0, '\$options': 'i' } }, { 'username': { '\$regex': ?0, '\$options': 'i' } } ], 'email': { '\$ne': null } }")
    fun findGoogleUsersByQuery(query: String, pageable: Pageable): Page<UserDocument>

}