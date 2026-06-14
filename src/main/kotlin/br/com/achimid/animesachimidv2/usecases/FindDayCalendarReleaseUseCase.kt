package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.CalendarItem
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class FindDayCalendarReleaseUseCase(
    private val findCalendarReleaseUseCase: FindCalendarReleaseUseCase,
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable("calendarCache")
    fun execute(dayIndex: Int): List<CalendarItem> {
        val schedule = findCalendarReleaseUseCase.execute().schedule
        return schedule.entries.elementAtOrNull(dayIndex)?.value ?: emptyList()
    }

}