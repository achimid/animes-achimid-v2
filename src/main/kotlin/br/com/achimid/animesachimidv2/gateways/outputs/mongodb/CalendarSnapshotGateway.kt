package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.Calendar
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.CalendarSnapshotDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.CalendarSnapshotMongoRepository
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class CalendarSnapshotGateway(
    private val repo: CalendarSnapshotMongoRepository,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun save(calendar: Calendar) {
        try {
            repo.save(CalendarSnapshotDocument(data = objectMapper.writeValueAsString(calendar)))
        } catch (e: Exception) {
            logger.warn("Falha ao salvar snapshot do calendário: ${e.message}")
        }
    }

    /** Retorna o snapshot se existe e foi salvo há menos de [maxAgeHours] horas. */
    fun findRecent(maxAgeHours: Long = 12): Calendar? {
        return try {
            val doc = repo.findById("full_calendar").orElse(null) ?: return null
            if (doc.savedAt.isBefore(Instant.now().minus(maxAgeHours, ChronoUnit.HOURS))) return null
            objectMapper.readValue<Calendar>(doc.data)
        } catch (e: Exception) {
            logger.warn("Falha ao carregar snapshot do calendário: ${e.message}")
            null
        }
    }
}
