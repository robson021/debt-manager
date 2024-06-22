package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.User
import com.github.robson021.debtmanager.extensions.GoogleUserDetails
import com.github.robson021.debtmanager.logger
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val dbClient: DatabaseClient,
) {
    suspend fun addGoogleUserIfNotPresent(user: GoogleUserDetails) {
        val userSub = user.sub

        val bySub = dbClient.sql("select sub from USERS u where u.sub = :sub")
            .bind("sub", userSub)
            .fetch()
            .awaitSingleOrNull()

        if (bySub != null) {
            log.debug("User with sub: $userSub already exists.")
            return
        }

        dbClient.sql("insert into USERS (sub, name, email) values ( :sub, :name, :email )")
            .bind("sub", userSub)
            .bind("name", user.name)
            .bind("email", user.email)
            .fetch()
            .awaitRowsUpdated()

        log.info("Saved new user: $user.")
    }

    suspend fun findUserBySub(sub: String): User {
        val user = dbClient.sql("select * from USERS where sub = :sub")
            .bind("sub", sub)
            .mapProperties(User::class.java)
            .awaitSingle()

        log.info("Found user: $user.")
        return user
    }

    companion object {
        private val log by logger()
    }
}
