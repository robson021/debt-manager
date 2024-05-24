package com.github.robson021.debtmanager.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authorize: ServerHttpSecurity.AuthorizeExchangeSpec ->
                authorize
//                    .pathMatchers("/api/**").hasRole("ACTIVE-USER")
                    .anyExchange().authenticated()
            }.oauth2Login(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(
        @Value("\${spring.security.oauth2.client.registration.google.client-id}") clientId: String,
        @Value("\${spring.security.oauth2.client.registration.google.client-secret}") clientSecret: String,
    ) = InMemoryReactiveClientRegistrationRepository(
        CommonOAuth2Provider.GOOGLE
            .getBuilder("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build()!!
    )

}
