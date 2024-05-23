package com.github.robson021.debtmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

@EnableWebFluxSecurity
@EnableR2dbcRepositories
@SpringBootApplication
class DebtManagerApplication

fun main(args: Array<String>) {
    runApplication<DebtManagerApplication>(*args)
}
