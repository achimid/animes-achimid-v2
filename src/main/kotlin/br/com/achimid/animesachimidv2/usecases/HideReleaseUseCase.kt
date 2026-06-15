package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class HideReleaseUseCase(val releaseGateway: ReleaseGateway) {

    @Caching(evict = [
        CacheEvict("releasesCache", allEntries = true),
        CacheEvict("statsCache", allEntries = true),
    ])
    fun execute(id: String) {
        val release = releaseGateway.findById(id) ?: return
        releaseGateway.save(release.copy(hidden = true))
    }
}
