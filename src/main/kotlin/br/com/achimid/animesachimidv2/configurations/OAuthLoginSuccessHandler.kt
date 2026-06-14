package br.com.achimid.animesachimidv2.configurations

import br.com.achimid.animesachimidv2.usecases.LoginWithGoogleUseCase
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

/**
 * Após o login Google bem-sucedido (F1): resolve/mescla a conta e **repõe** o cookie `user_id`
 * apontando para o id da conta, para que os fluxos de favoritar/comentar (baseados nesse cookie)
 * passem a operar sobre a conta autenticada. Em seguida redireciona para a home.
 */
@Component
class OAuthLoginSuccessHandler(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal
        if (principal is OidcUser && principal.email != null) {
            val guestId = request.cookies?.firstOrNull { it.name == "user_id" }?.value
            val account = loginWithGoogleUseCase.execute(
                email = principal.email,
                name = principal.fullName ?: principal.getAttribute("name"),
                picture = principal.picture,
                googleId = principal.subject,
                guestUserId = guestId
            )

            val cookie = Cookie("user_id", account.id)
            cookie.path = "/"
            cookie.maxAge = 60 * 60 * 24 * 365
            response.addCookie(cookie)
        }

        response.sendRedirect("/")
    }
}
