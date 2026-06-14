package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.SiteIntegrationResponse
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegration
import br.com.achimid.animesachimidv2.usecases.ExtractionTaskUseCase
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.ProcessIntegrationCallbackUserCase
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture.runAsync


@RestController
@RequestMapping("/api/v1/site/integration")
class SiteIntegrationAPIController(
    private val extractionTaskUseCase: ExtractionTaskUseCase,
    private val retrieveSiteIntegrations: FindSiteIntegrationsUseCase,
    private val processIntegrationCallbackUserCase: ProcessIntegrationCallbackUserCase,
    private val adminAccessChecker: AdminAccessChecker,
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    @ResponseStatus(OK)
    fun find(
        @RequestParam(required = false) query: String? = null,
    ): SiteIntegrationResponse = SiteIntegrationResponse(retrieveSiteIntegrations.execute(query))

    @ResponseStatus(OK)
    @PostMapping("/callback")
    fun callbackIntegration(@RequestBody callbackIntegration: CallbackIntegration) {
        runAsync { processIntegrationCallbackUserCase.execute(callbackIntegration) }
    }

    @ResponseStatus(OK)
    @PostMapping("/extraction/all/run")
    @RateLimiter(name = "extractionApiLimiter", fallbackMethod = "runAllExtractionsRateLimiterFallback")
    fun runAllExtractions() {
        extractionTaskUseCase.executeFastQueueMonitoring()
        extractionTaskUseCase.executeMediumQueueMonitoring()
        extractionTaskUseCase.executeSlowQueueMonitoring()
    }

    fun runAllExtractionsRateLimiterFallback(t: Throwable) {
        logger.warn("Rate limiter for endpoint /extraction/all/run triggered")
    }

    @ResponseStatus(OK)
    @PostMapping("/extraction/{name}/run")
    fun runSingleExtraction(
        @PathVariable name: String,
        @CookieValue(value = "user_id", required = false) userId: String?
    ) {
        adminAccessChecker.requireAdmin(userId)
        runAsync { extractionTaskUseCase.executeSingle(name) }
    }
}
