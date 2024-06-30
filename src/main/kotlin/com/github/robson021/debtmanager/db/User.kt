package com.github.robson021.debtmanager.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("USERS")
data class User(
    @Id val id: Int,
    val sub: BigDecimal,
    val name: String,
    val email: String,
//    val groups: List<Group>,
)
