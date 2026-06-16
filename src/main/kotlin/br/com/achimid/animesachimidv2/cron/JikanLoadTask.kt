package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.AnimeRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class JikanLoadTask(
    val animeRepository: AnimeRepository,
    val animeGateway: AnimeGateway,
    val jikanGateway: JikanAPIGateway,
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @Async
    @EventListener(ApplicationReadyEvent::class)
    @Scheduled(cron = "0 0 4 * * ?")
    @Caching(evict = [
        CacheEvict("currentSeasonCache", allEntries = true),
        CacheEvict("featuredAnimeCache", allEntries = true),
        CacheEvict("calendarCache", allEntries = true),
        CacheEvict("calendarSearchCache", allEntries = true),
    ])
    fun loadCurrentSeason() {
        logger.info("Loading current season animes from Jikan...")
        val animes = jikanGateway.findCurrentSeason()
        if (animes.isEmpty()) { logger.warn("No season animes returned from Jikan"); return }
        animes.chunked(10).forEach { chunk -> animeGateway.saveAll(chunk) }
        logger.info("Current season loaded: ${animes.size} animes saved")
    }

    // Atualiza 4 animes por minuto (nulls/mais antigos primeiro), respeitando o rate limit do Jikan.
    // fixedDelay garante que a próxima execução só começa após a atual terminar.
    @Scheduled(fixedDelay = 60_000)
    @Caching(evict = [
        CacheEvict("animeCache", allEntries = true),
        CacheEvict("animesCache", allEntries = true),
        CacheEvict("featuredAnimeCache", allEntries = true),
    ])
    fun alwaysUpdateSourceJikan() {
        val animes = animeRepository.findTop4ByOrderByJikanSyncedAtAsc()

        for (anime in animes) {
            val jikan = runCatching { jikanGateway.findById(anime.id!!) }.getOrNull() ?: continue
            animeGateway.saveAll(listOf(jikan))
            logger.info("Jikan sync: ${anime.name}")
        }
    }

}
