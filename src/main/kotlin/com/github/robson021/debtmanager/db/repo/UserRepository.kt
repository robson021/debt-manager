package com.github.robson021.debtmanager.db.repo

import com.github.robson021.debtmanager.db.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<User, String>
