package br.com.achimid.animesachimidv2.configurations

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PageAccessGateway
import jakarta.servlet.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.atomic.AtomicLong


@Component
class RequestVisitCounterConfig(
    val pageAccessGateway: PageAccessGateway
) : Filter {

    val PATHS = listOf(
        Regex("/"),
        Regex("/catalog"),
        Regex("^/anime")
    )

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val path = httpRequest.requestURI

        chain.doFilter(request, response)

        if (PATHS.any{ path.matches(it) }) runAsync { pageAccessGateway.incrementCounter() }
    }
}