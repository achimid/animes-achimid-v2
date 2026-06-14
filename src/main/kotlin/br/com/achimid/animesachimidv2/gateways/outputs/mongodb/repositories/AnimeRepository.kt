package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.domains.CommentStatus
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeCommentDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeStatusDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository

@Repository
interface AnimeRepository: MongoRepository<AnimeDocument, String> {

    fun findBySlug(slug: String): AnimeDocument?

    fun findByNameContainingIgnoreCase(query: String, pageRequest: PageRequest): Page<AnimeDocument>

    @Query("{ 'id' : ?0 }")
    @Update("{ '\$inc' : { 'accessCounter' : 1 } }")
    fun incrementAccessCounter(id: String)

    @Query("{ 'id' : ?0 }")
    @Update("{ '\$push' : { 'comments' : ?1 } }")
    fun addComment(id: String, comment: AnimeCommentDocument)

    fun findTop4ByOrderByUpdatedAtAsc(): List<AnimeDocument>

    fun findByCommentsStatus(status: CommentStatus): List<AnimeDocument>

    fun findByStatus(status: AnimeStatusDocument): List<AnimeDocument>

    fun findByTagsIn(tags: Collection<String>, pageRequest: PageRequest): Page<AnimeDocument>

    fun findByTagsInAndNameContainingIgnoreCase(tags: Collection<String>, name: String, pageRequest: PageRequest): Page<AnimeDocument>

    @Query("{ '\$or': [ { 'descriptionPtBr': null }, { 'descriptionPtBr': '' }, { 'synopsisPtBr': null }, { 'synopsisPtBr': '' } ] }")
    fun findAllWithoutTranslation(): List<AnimeDocument>

    @Query("{ 'comments': { '\$exists': true, '\$not': { '\$size': 0 } } }")
    fun findByCommentsNotEmpty(): List<AnimeDocument>

}