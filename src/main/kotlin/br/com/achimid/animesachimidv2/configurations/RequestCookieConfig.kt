package br.com.achimid.animesachimidv2.configurations

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync


@Component
class RequestCookieConfig(
    val userGateway: UserGateway
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cookies = request.cookies
        val hasUserCookie = cookies?.any { it.name == "user_id" } ?: false
        val userAgent = request.getHeader("User-Agent")

        if (!hasUserCookie && isBrowserRequest(userAgent)) {
            val userId = UUID.randomUUID().toString()
            val cookie = Cookie("user_id", userId)
            cookie.path = "/"
            response.addCookie(cookie)

            runAsync { userGateway.save(User(id = userId)) }
        }

        filterChain.doFilter(request, response)
    }

    private fun isBrowserRequest(userAgent: String?): Boolean {
        return userAgent != null &&
                !userAgent.contains("bot/", ignoreCase = true) &&
                (
                        userAgent.contains("Chrome", ignoreCase = true) ||
                        userAgent.contains("Safari", ignoreCase = true) ||
                        userAgent.contains("Firefox", ignoreCase = true) ||
                        userAgent.contains("Edg", ignoreCase = true) ||
                        userAgent.contains("Mobile", ignoreCase = true)
                )
    }
}