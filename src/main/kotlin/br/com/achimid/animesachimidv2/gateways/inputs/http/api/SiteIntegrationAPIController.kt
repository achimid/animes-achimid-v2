package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.SiteIntegrationResponse
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegration
import br.com.achimid.animesachimidv2.usecases.FindSiteIntegrationsUseCase
import br.com.achimid.animesachimidv2.usecases.ProcessIntegrationCallbackUserCase
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/site/integration")
class SiteIntegrationAPIController(
    val retrieveSiteIntegrations: FindSiteIntegrationsUseCase,
    val processIntegrationCallbackUserCase: ProcessIntegrationCallbackUserCase
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
        processIntegrationCallbackUserCase.execute(callbackIntegration)
    }
}
