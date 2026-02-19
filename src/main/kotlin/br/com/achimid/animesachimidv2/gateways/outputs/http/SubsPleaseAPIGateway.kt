package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.Calendar
import br.com.achimid.animesachimidv2.domains.CalendarToday
import br.com.achimid.animesachimidv2.gateways.outputs.http.subsplease.SubsPleaseAPIClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@Component
class SubsPleaseAPIGateway(
    private val objectMapper: ObjectMapper,
    val subsPleaseAPIClient: SubsPleaseAPIClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    fun findFullSchedule(): Calendar {
        logger.info("Searching for all schedules")

        val schedulers = subsPleaseAPIClient.findFullSchedule()
        val schedulerTyped = objectMapper.readValue<Calendar>(schedulers.body.toString())

        return schedulerTyped
    }

    fun findTodaySchedule(): CalendarToday {
        logger.info("Searching for today schedules")

        val schedulers = subsPleaseAPIClient.findTodaySchedule()
        val schedulerTyped = objectMapper.readValue<CalendarToday>(schedulers.body.toString())

        return schedulerTyped
    }

}