package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegration
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegrationExecutionResult
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture.runAsync

@Component
class ProcessIntegrationCallbackUserCase(
    val createReleaseUserCase: CreateReleaseUserCase,
    val siteIntegrationGateway: SiteIntegrationGateway
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    fun execute(callbackIntegration: CallbackIntegration) {
        try {
            val siteName = callbackIntegration.request.ref

            if (callbackIntegration.execution == null || callbackIntegration.execution.result == null) {
                logger.error("[$siteName] Execution failed", callbackIntegration)
                return siteIntegrationGateway.updateByName(siteName, false)
            }

            siteIntegrationGateway.updateByName(siteName, true)

            callbackIntegration.execution.result
                .filter(this::createEventIntegration)
                .forEach(this::createRelease)
        } catch (ex: RuntimeException) {
            logger.error("Error on process release: {}", callbackIntegration, ex)
            throw ex
        }
    }

    fun createEventIntegration(result: CallbackIntegrationExecutionResult): Boolean {
        return try {
            siteIntegrationGateway.createEvenIntegration(result)
        } catch (_: RuntimeException) {
            true
        }
    }

    fun createRelease(result: CallbackIntegrationExecutionResult) {
        try {
            createReleaseUserCase.execute(result)
        } catch (ex: RuntimeException) {
            logger.error("Error on create release: {}", result, ex)
        }
    }

}