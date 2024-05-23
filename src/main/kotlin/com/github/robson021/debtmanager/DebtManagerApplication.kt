package com.github.robson021.debtmanager

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@EnableR2dbcRepositories
@SpringBootApplication
class DebtManagerApplication

fun main(args: Array<String>) {
    runApplication<DebtManagerApplication>(*args)
}

@Bean
fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    http
        .authorizeExchange { authorize: ServerHttpSecurity.AuthorizeExchangeSpec ->
            authorize.anyExchange().authenticated()
        }
        .oauth2Login(Customizer.withDefaults())
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
