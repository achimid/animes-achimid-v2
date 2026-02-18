package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.CalendarItem
import br.com.achimid.animesachimidv2.usecases.FindDayCalendarReleaseUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarAPIController(
    val findCalendarDayCalendarReleaseUseCase: FindDayCalendarReleaseUseCase
) {

    @GetMapping
    @ResponseStatus(OK)
    fun fullCalendar(
        @RequestParam(value = "dayIndex", required = true) dayIndex: Int,
    ): List<CalendarItem> {
        return findCalendarDayCalendarReleaseUseCase.execute(dayIndex)
    }

}
