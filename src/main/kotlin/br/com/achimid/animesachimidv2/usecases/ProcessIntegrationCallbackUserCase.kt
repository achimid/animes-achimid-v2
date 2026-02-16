package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegration
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
                .filter(siteIntegrationGateway::createEvenIntegration)
                .forEach(createReleaseUserCase::execute)
        } catch (ex: RuntimeException) {
            logger.error("Error on process release: {}", callbackIntegration, ex)
            throw ex
        }
    }

}