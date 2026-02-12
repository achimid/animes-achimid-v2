package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AnimeRepository: MongoRepository<AnimeDocument, String> {

    fun findBySlug(slug: String): AnimeDocument?

    fun findByNameContainingIgnoreCase(query: String, pageRequest: PageRequest): Page<AnimeDocument>

}