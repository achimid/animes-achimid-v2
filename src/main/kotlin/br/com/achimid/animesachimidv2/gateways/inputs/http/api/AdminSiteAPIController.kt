package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.SiteIntegrationType
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * API administrativa de sites de scraping (FUNC-04), restrita a administradores.
 * Possível graças ao F2 (sites no MongoDB) — habilitar/desabilitar e mudar de fila sem redeploy.
 */
@RestController
@RequestMapping("/api/v1/admin/sites")
class AdminSiteAPIController(
    val siteIntegrationGateway: SiteIntegrationGateway,
    val adminAccessChecker: AdminAccessChecker,
) {

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/{name}/enable")
    fun enable(@PathVariable name: String, @CookieValue(value = "user_id", required = false) userId: String? = null) {
        adminAccessChecker.requireAdmin(userId)
        siteIntegrationGateway.setEnabled(name, true)
    }

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/{name}/disable")
    fun disable(@PathVariable name: String, @CookieValue(value = "user_id", required = false) userId: String? = null) {
        adminAccessChecker.requireAdmin(userId)
        siteIntegrationGateway.setEnabled(name, false)
    }

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/{name}/type/{type}")
    fun setType(
        @PathVariable name: String,
        @PathVariable type: SiteIntegrationType,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ) {
        adminAccessChecker.requireAdmin(userId)
        siteIntegrationGateway.setType(name, type)
    }
}
