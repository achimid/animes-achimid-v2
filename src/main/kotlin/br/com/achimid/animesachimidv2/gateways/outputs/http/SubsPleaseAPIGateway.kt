package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.Schedule
import br.com.achimid.animesachimidv2.gateways.outputs.http.subsplease.SubsPleaseAPIClient
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@Component
class SubsPleaseAPIGateway(
    private val objectMapper: ObjectMapper,
    val subsPleaseAPIClient: SubsPleaseAPIClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable("scheduleCache")
    fun findFullSchedule(): Schedule {
        logger.info("Searching for all schedules")

        val schedulers = subsPleaseAPIClient.findFullSchedule()
        val schedulerTyped = objectMapper.readValue<Schedule>(schedulers.body.toString())

        return schedulerTyped
    }

}