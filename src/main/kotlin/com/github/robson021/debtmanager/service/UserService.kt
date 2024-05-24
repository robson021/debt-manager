package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.User
import com.github.robson021.debtmanager.db.repo.UserRepository
import com.github.robson021.debtmanager.extensions.GoogleUser
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    suspend fun addGoogleUser(user: GoogleUser): User {
        // todo
        throw UnsupportedOperationException()
    }
}
