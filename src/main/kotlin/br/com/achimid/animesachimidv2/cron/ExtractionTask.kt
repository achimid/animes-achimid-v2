package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.usecases.ExtractionTaskUseCase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["extraction-tasks.enabled"], havingValue = "true", matchIfMissing = true)
class ExtractionTask(
    val extractionTaskUseCase: ExtractionTaskUseCase
) {

    @Scheduled(fixedRate = 1000 * 60 * 15)
    fun executeFastQueueMonitoring() {
        extractionTaskUseCase.executeFastQueueMonitoring()
    }

    @Scheduled(fixedRate = 1000 * 60 * 30)
    fun executeMediumQueueMonitoring() {
        extractionTaskUseCase.executeMediumQueueMonitoring()
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    fun executeSlowQueueMonitoring() {
        extractionTaskUseCase.executeSlowQueueMonitoring()
    }

    @Scheduled(fixedRate = 1000 * 60 * 1)
    fun executeMatchAnimeReleaseHourMonitoring() {
        extractionTaskUseCase.executeMatchAnimeReleaseHourMonitoring()
    }

}