package br.com.achimid.animesachimidv2.configurations

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.CalendarSnapshotGateway
import br.com.achimid.animesachimidv2.usecases.FindCalendarReleaseUseCase
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Aquece o cache do calendário de forma não-bloqueante na inicialização.
 * Caminho rápido: carrega snapshot do MongoDB (< 100ms) → Caffeine.
 * Se não houver snapshot recente: busca SubsPlease em background.
 */
@Component
class CalendarCacheWarmer(
    private val findCalendarReleaseUseCase: FindCalendarReleaseUseCase,
    private val snapshotGateway: CalendarSnapshotGateway,
    private val cacheManager: CacheManager,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun warmCache() {
        CompletableFuture.runAsync {
            try {
                val snapshot = snapshotGateway.findRecent(maxAgeHours = 12)
                if (snapshot != null) {
                    cacheManager.getCache("calendarCache")?.put(SimpleKey.EMPTY, snapshot)
                    logger.info("Calendário aquecido do snapshot MongoDB (${snapshot.schedule.values.sumOf { it.size }} itens)")
                } else {
                    logger.info("Sem snapshot recente do calendário; buscando do SubsPlease em background...")
                    findCalendarReleaseUseCase.executeAndSave()
                }
            } catch (e: Exception) {
                logger.warn("Falha ao aquecer cache do calendário na inicialização: ${e.message}")
            }
        }
    }
}
