package br.com.achimid.animesachimidv2.configurations

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class RequestMetricConfig : Filter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val url = httpRequest.requestURI
        val method = httpRequest.method

        val duration = measureTimeMillis {
            chain.doFilter(request, response)
        }

        logger.info("RequestCallbackIntegration: [$method] $url - Completed in ${duration}ms")
    }
}