package com.github.robson021.debtmanager.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
data class User(
    @Id val id: Int,
    val sub: String,
    val name: String,
    val email: String,
//    val groups: List<Group>,
)
