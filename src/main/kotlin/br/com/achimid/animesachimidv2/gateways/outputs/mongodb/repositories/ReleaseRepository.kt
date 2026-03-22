package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.ReleaseDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ReleaseRepository: MongoRepository<ReleaseDocument, String> {

    fun findByTitleContainingIgnoreCase(query: String, pageRequest: PageRequest): Page<ReleaseDocument>

    fun findByAnimeIdOrderByEpisodeDesc(animeId: String): List<ReleaseDocument>

    fun findByAnimeIdAndEpisode(animeId: String, episodeNumber: String): List<ReleaseDocument>
}