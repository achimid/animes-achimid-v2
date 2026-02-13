package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.CalendarRelease
import br.com.achimid.animesachimidv2.gateways.outputs.http.SubsPleaseAPIGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale.ENGLISH

@Component
class FindTodayCalendarReleaseUseCase(
    private val subsPleaseAPIGateway: SubsPleaseAPIGateway
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(): CalendarRelease? {
        try {
            val todayName = LocalDate.now().dayOfWeek.getDisplayName(FULL, ENGLISH)
            val todaySchedule = subsPleaseAPIGateway.findFullSchedule().schedule.getValue(todayName)

            return CalendarRelease(todaySchedule)
        } catch (ex: RuntimeException) {
            logger.error("Error while fetching schedule info", ex)
            return null
        }
    }

}