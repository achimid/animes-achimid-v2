package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.http.PuppeteerAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

@Component
class ExtractionTaskUseCase(
    val puppeteerAPIGateway: PuppeteerAPIGateway,
    val siteIntegrationGateway: SiteIntegrationGateway,
    val todayCalendarReleaseUseCase: FindTodayCalendarReleaseUseCase
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    fun executeSingle(name: String) {
        val site = siteIntegrationGateway.findByName(name)
        puppeteerAPIGateway.execute(site)
        logger.info("Extração individual disparada para: $name")
    }

    fun executeFastQueueMonitoring() {
        siteIntegrationGateway.findFast().forEach(puppeteerAPIGateway::execute)
    }

    fun executeMediumQueueMonitoring() {
        siteIntegrationGateway.findMedium().forEach(puppeteerAPIGateway::execute)
    }

    fun executeSlowQueueMonitoring() {
        siteIntegrationGateway.findSlow().forEach(puppeteerAPIGateway::execute)
    }

    fun executeMatchAnimeReleaseHourMonitoring() {

        val todayCalendar = todayCalendarReleaseUseCase.execute()
        val pattern = ofPattern("HH:mm")
        val currentHourMinute = now().format(pattern)
        val currentHourMinutePlus2 = now().minusMinutes(2).format(pattern)
        val currentHourMinutePlus5 = now().minusMinutes(5).format(pattern)

        todayCalendar.releasesToday.firstOrNull {
            it.time == currentHourMinute || it.time == currentHourMinutePlus2 || it.time == currentHourMinutePlus5
        }?.let {
            siteIntegrationGateway.findFast().forEach(puppeteerAPIGateway::execute)
        }

    }

}
