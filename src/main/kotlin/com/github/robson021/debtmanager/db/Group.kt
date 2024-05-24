package com.github.robson021.debtmanager.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("GROUPS")
data class Group(
    @Id val id: Long,
    val groupName: String,
)
