package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ReleaseRepository: MongoRepository<ReleaseDocument, String> {

    @Query("{ 'hidden': { '\$ne': true } }")
    fun findVisible(pageRequest: PageRequest): Page<ReleaseDocument>

    @Query("{ 'hidden': { '\$ne': true }, 'title': { '\$regex': ?0, '\$options': 'i' } }")
    fun findVisibleByTitleContaining(query: String, pageRequest: PageRequest): Page<ReleaseDocument>

    @Query(value = "{ 'hidden': { '\$ne': true }, 'animeId': ?0 }", sort = "{ 'episode': -1 }")
    fun findVisibleByAnimeIdOrderByEpisodeDesc(animeId: String): List<ReleaseDocument>

    fun findByAnimeIdAndEpisode(animeId: String, episodeNumber: String): List<ReleaseDocument>

    @Query(value = "{ 'createdAt': { '\$gt': ?0 }, 'hidden': { '\$ne': true } }", count = true)
    fun countVisibleByCreatedAtAfter(date: Instant): Long

    @Query("{ 'animeId': ?0 }")
    @Update("{ '\$set': { 'animeImage': ?1 } }")
    fun updateAnimeImageByAnimeId(animeId: String, imageUrl: String)

    @Query("{ 'needsReview': true }")
    fun findNeedingReview(pageRequest: PageRequest): Page<ReleaseDocument>
}