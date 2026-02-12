package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.AnimeDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.AnimeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.PageRequest.of
import org.springframework.stereotype.Component

@Component
class AnimeGateway(
    val animeRepository: AnimeRepository,
    val mapper: AnimeDocumentMapper
) {

    @Cacheable("animesCache")
    fun findAll(pageRequest: PageRequest): Page<Anime> {
        return animeRepository.findAll(pageRequest).map(mapper::fromDocument)
    }

    @Cacheable("recommendationsCache")
    fun findRandom(size: Int = 6): Page<Anime> {
        return animeRepository.findAll(of((0..300).random() , size)).map(mapper::fromDocument)
    }

    @Cacheable("animeSearchCache")
    fun findByName(pageRequest: PageRequest, query: String): Page<Anime> {
        return animeRepository.findByNameContainingIgnoreCase(query, pageRequest).map(mapper::fromDocument)
    }

    fun findLastSlugs(): List<String> = animeRepository.findAll().map { it.slug }

    fun findBySlug(slug: String): Anime? = animeRepository.findBySlug(slug)?.let(mapper::fromDocument)

    fun findById(id: String): Anime? = animeRepository.findById(id).get().let(mapper::fromDocument)
}