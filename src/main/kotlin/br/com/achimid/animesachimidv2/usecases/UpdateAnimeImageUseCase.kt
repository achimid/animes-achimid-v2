package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class UpdateAnimeImageUseCase(
    val animeGateway: AnimeGateway,
    val releaseGateway: ReleaseGateway,
) {

    @Caching(evict = [
        CacheEvict("animeCache", allEntries = true),
        CacheEvict("animesCache", allEntries = true),
        CacheEvict("featuredAnimeCache", allEntries = true),
        CacheEvict("releasesCache", allEntries = true),
    ])
    fun execute(slug: String, imageUrl: String): Boolean {
        val anime = animeGateway.findBySlug(slug) ?: return false
        animeGateway.save(anime.copy(imageUrl = imageUrl))
        releaseGateway.updateAnimeImage(anime.id, imageUrl)
        return true
    }
}
