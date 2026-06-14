package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

/**
 * Visão mínima do usuário atual exposta aos templates do site (F1/FUNC-06).
 * `authenticated = false` representa o visitante anônimo (cookie `user_id`).
 */
data class CurrentUserView(
    val authenticated: Boolean,
    val name: String? = null,
    val picture: String? = null
)

/**
 * Injeta `currentUser` e `oauthEnabled` no Model de todos os controllers de site,
 * para o header renderizar o estado de login de forma consistente em todas as páginas.
 */
@ControllerAdvice(basePackages = ["br.com.achimid.animesachimidv2.gateways.inputs.http.site"])
class CurrentUserAdvice(
    private val clientRegistrations: ObjectProvider<ClientRegistrationRepository>,
    @Value("\${webpush.vapid-public-key}") private val vapidPublicKey: String,
) {

    @ModelAttribute("currentUser")
    fun currentUser(): CurrentUserView {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        if (principal is OidcUser) {
            return CurrentUserView(
                authenticated = true,
                name = principal.fullName ?: principal.getAttribute("name") ?: principal.email,
                picture = principal.picture
            )
        }
        return CurrentUserView(authenticated = false)
    }

    @ModelAttribute("oauthEnabled")
    fun oauthEnabled(): Boolean = clientRegistrations.getIfAvailable() != null

    @ModelAttribute("vapidPublicKey")
    fun vapidPublicKey(): String = vapidPublicKey
}
