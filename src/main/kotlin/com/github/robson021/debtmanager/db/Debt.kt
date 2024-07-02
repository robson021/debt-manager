package com.github.robson021.debtmanager.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("DEBTS")
data class Debt(
    @Id val id: Int,
    val amount: BigDecimal,
    val lenderId: Int,
    val borrowerId: Int,
    val groupId: Int,
)
