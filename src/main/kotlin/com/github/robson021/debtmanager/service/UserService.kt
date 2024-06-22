package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.User
import com.github.robson021.debtmanager.db.repo.UserRepository
import com.github.robson021.debtmanager.extensions.GoogleUser
import com.github.robson021.debtmanager.logger
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {
    suspend fun addGoogleUser(user: GoogleUser): User {
        val saved = userRepository.save(User(null, user.sub, user.name, user.email, emptyList())).awaitSingle()
        log.info("Saved user: $saved")
        return saved
    }

    companion object {
        private val log by logger()
    }
}
