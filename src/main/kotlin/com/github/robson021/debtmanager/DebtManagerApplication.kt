package com.github.robson021.debtmanager

import com.github.robson021.debtmanager.extension.userDetails
import com.github.robson021.debtmanager.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableWebFluxSecurity
@EnableR2dbcRepositories
@SpringBootApplication
class DebtManagerApplication

fun <R : Any> R.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(this::class.java.name.removeSuffix("\$Companion")) }

fun main(args: Array<String>) {
    runApplication<DebtManagerApplication>(*args)
}

@RestController
class PublicController(
    private val userService: UserService,
) {

    @GetMapping("/hello")
    suspend fun hello(token: OAuth2AuthenticationToken) = "Hello, ${token.userDetails().name}!"

    @GetMapping("/user-details")
    suspend fun userDetails(token: OAuth2AuthenticationToken) = token.userDetails()

    @GetMapping("/activate-user")
    suspend fun activateUser(token: OAuth2AuthenticationToken) {
        userService.addGoogleUserIfNotPresent(token.userDetails())
    }

}

@RestController
@RequestMapping("/api")
class ApiController {

    @PostMapping("/create-group")
    suspend fun apiTest(token: OAuth2AuthenticationToken) {
        // todo
    }

}
