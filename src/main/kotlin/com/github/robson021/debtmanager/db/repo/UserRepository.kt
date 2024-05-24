package com.github.robson021.debtmanager.db.repo

import com.github.robson021.debtmanager.db.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findBySub(sub: BigDecimal): Mono<User>
}
