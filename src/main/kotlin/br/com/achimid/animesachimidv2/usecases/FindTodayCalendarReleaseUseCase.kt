package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.CalendarRelease
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale.ENGLISH

@Component
class FindTodayCalendarReleaseUseCase(
    private val findCalendarReleaseUseCase: FindCalendarReleaseUseCase
) {

    fun execute(): CalendarRelease? {
        val schedule = findCalendarReleaseUseCase.execute() ?: return null

        val todayName = LocalDate.now().dayOfWeek.getDisplayName(FULL, ENGLISH)
        val todaySchedule = schedule.schedule.getValue(todayName)

        return CalendarRelease(todaySchedule)
    }

}