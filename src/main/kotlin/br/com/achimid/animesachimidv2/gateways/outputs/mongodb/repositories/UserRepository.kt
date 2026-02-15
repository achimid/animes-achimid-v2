package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.UserDocument
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


}