package com.github.robson021.debtmanager.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class UserCache(private val dbClient: DatabaseClient) {
    private val cache = ConcurrentHashMap<String, Int>()

    suspend fun getUserId(sub: String): Int {
        return cache.getOrPut(sub) {
            return dbClient.sql("select id from USERS u where u.sub = :sub")
                .bind("sub", sub)
                .fetch()
                .first()
                .awaitSingle()["id"] as Int
        }
    }
}
