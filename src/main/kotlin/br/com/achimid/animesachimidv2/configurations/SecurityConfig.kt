package br.com.achimid.animesachimidv2.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        oAuthLoginSuccessHandler: OAuthLoginSuccessHandler
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .logout { it.logoutUrl("/logout").logoutSuccessUrl("/").permitAll() }
            .oauth2Login { it.successHandler(oAuthLoginSuccessHandler) }

        return http.build()
    }
}
