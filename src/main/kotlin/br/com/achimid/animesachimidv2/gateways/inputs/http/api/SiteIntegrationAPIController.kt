package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.SiteIntegrationResponse
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegrationRequest
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/site/integration")
class SiteIntegrationAPIController(
    val retrieveSiteIntegrations: FindSiteIntegrationsUseCase
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    @ResponseStatus(OK)
    fun find(): SiteIntegrationResponse = SiteIntegrationResponse(retrieveSiteIntegrations.execute())

    @ResponseStatus(OK)
    @PostMapping("/callback")
    fun callbackIntegration(@RequestBody callbackIntegrationRequest: CallbackIntegrationRequest) {
        logger.info("Starting callback integration: ${callbackIntegrationRequest}")
    }
}
