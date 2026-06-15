package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class RefreshAnimeUseCase(
    val animeGateway: AnimeGateway,
    val jikanAPIGateway: JikanAPIGateway,
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @Caching(evict = [
        CacheEvict("animeCache", allEntries = true),
        CacheEvict("animesCache", allEntries = true),
        CacheEvict("featuredAnimeCache", allEntries = true),
        CacheEvict("calendarCache", allEntries = true),
        CacheEvict("recommendationsCache", allEntries = true),
    ])
    fun execute(slug: String): Boolean {
        val anime = animeGateway.findBySlug(slug) ?: return false
        val jikan = jikanAPIGateway.findById(anime.id) ?: return false
        animeGateway.saveAll(listOf(jikan))
        logger.info("Anime atualizado do Jikan: ${anime.name}")
        return true
    }
}
