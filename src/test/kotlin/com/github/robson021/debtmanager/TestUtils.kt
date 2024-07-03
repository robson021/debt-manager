package com.github.robson021.debtmanager

import com.github.robson021.debtmanager.extension.GoogleUserDetails
import com.github.robson021.debtmanager.service.UserService

fun getTestUser() = GoogleUserDetails("123", "test-user", "avatar", "scope", "test@mail.com")
fun getTestUser(id: String) = GoogleUserDetails(id, "test-user-$id", "avatar", "scope", "$id@mail.com")

suspend fun UserService.addUsersBatch(vararg users: GoogleUserDetails) {
    users.forEach { this.addGoogleUserIfNotPresent(it) }
}
