package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.gateways.outputs.http.PuppeteerAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import br.com.achimid.animesachimidv2.usecases.FindTodayCalendarReleaseUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

@Component
class ExtractionTask(
    val puppeteerAPIGateway: PuppeteerAPIGateway,
    val siteIntegrationGateway: SiteIntegrationGateway,
    val todayCalendarReleaseUseCase: FindTodayCalendarReleaseUseCase
) {

    val logger = LoggerFactory.getLogger(this::class.java)

        @Scheduled(fixedRate = 1000 * 60 * 15)
    fun executeFastQueueMonitoring() {
        siteIntegrationGateway.findFast().forEach(puppeteerAPIGateway::execute)
    }

        @Scheduled(fixedRate = 1000 * 60 * 30)
    fun executeMediumQueueMonitoring() {
        siteIntegrationGateway.findMedium().forEach(puppeteerAPIGateway::execute)
    }

        @Scheduled(fixedRate = 1000 * 60 * 60)
    fun executeSlowQueueMonitoring() {
        siteIntegrationGateway.findSlow().forEach(puppeteerAPIGateway::execute)
    }

    @Scheduled(fixedRate = 1000 * 60 * 1)
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