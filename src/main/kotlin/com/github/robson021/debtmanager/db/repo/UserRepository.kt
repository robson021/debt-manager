package com.github.robson021.debtmanager.db.repo

import com.github.robson021.debtmanager.db.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    suspend fun findBySub(sub: BigDecimal): User
}
