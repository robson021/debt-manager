package com.github.robson021.debtmanager

import com.github.robson021.debtmanager.extensions.GoogleUserDetails

fun getTestUser() = GoogleUserDetails("123", "test-user", "avatar", "scope", "test@mail.com")
fun getTestUser(id: String) = GoogleUserDetails(id, "test-user-$id", "avatar", "scope", "$id@mail.com")
