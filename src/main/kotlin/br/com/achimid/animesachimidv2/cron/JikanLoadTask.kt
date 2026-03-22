package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.AnimeDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.AnimeRepository
import org.springframework.stereotype.Component

@Component
class JikanLoadTask(
    val animeRepository: AnimeRepository,
    val animeDocumentMapper: AnimeDocumentMapper,
    val jikanGateway: JikanAPIGateway,
) {

//    @Scheduled(fixedRate = 1000 * 60 * 1)
    fun alwaysUpdateSourceJikan() {
        val animes = animeRepository.findTop4ByOrderByUpdatedAtAsc()

        for (anime in animes) {
            val jikan = jikanGateway.findById(anime.id!!) ?: continue

            animeRepository.save(animeDocumentMapper.merge(anime, jikan))
        }
    }

}