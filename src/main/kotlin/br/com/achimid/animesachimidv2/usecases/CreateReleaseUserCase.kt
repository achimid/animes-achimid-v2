package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegrationExecutionResult
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class CreateReleaseUserCase(
    val releaseGateway: ReleaseGateway,
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @CacheEvict("releasesCache")
    fun execute(result: CallbackIntegrationExecutionResult) {
        logger.info("Release user creating a new even integration...")
    }

}