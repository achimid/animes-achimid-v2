package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Calendar
import br.com.achimid.animesachimidv2.gateways.outputs.http.SubsPleaseAPIGateway
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FindCalendarReleaseUseCase(
    private val searchUseCase: SearchUseCase,
    private val subsPleaseAPIGateway: SubsPleaseAPIGateway
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener(ApplicationReadyEvent::class)
    @Cacheable("calendarCache")
    fun execute(): Calendar? {
        try {
            val fullSchedule = subsPleaseAPIGateway.findFullSchedule()

            fullSchedule.schedule.onEachIndexed { index, entry ->
                entry.value.forEach { item ->
                    item.anime = searchUseCase.execute(item.title).firstOrNull()
                    item.dayIndex = index
                }
            }

            return fullSchedule
        } catch (ex: RuntimeException) {
            logger.error("Error while fetching schedule info", ex)
            return null
        }
    }

}