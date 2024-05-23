package com.github.robson021.debtmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableWebFluxSecurity
@EnableR2dbcRepositories
@SpringBootApplication
class DebtManagerApplication

fun main(args: Array<String>) {
    runApplication<DebtManagerApplication>(*args)
}

@RestController
class PublicController() {

    @GetMapping("/hello")
    suspend fun userDetails(token: OAuth2AuthenticationToken) = "Hello ${token.name}"


}

@RestController
@RequestMapping("/api")
class ApiController {

    @GetMapping("/test")
    suspend fun apiTest(token: OAuth2AuthenticationToken) = "test"

}
