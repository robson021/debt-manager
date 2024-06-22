package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.repo.UserRepository
import com.github.robson021.debtmanager.extensions.GoogleUser
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Service
@Transactional
class DebtService(
    private val userRepository: UserRepository,
    private val dbClient: DatabaseClient,
) {

    suspend fun createNewGroup(owner: GoogleUser, groupName: String) {
        if (!StringUtils.hasText(groupName)) {
            throw RuntimeException("Group name cannot be blank")
        }

        val user = userRepository.findBySub(owner.sub)

        val groupId = dbClient.sql("insert into GROUPS (name) values (:name)")
            .bind("name", groupName)
            .fetch()
            .first()
            .awaitSingle()["id"]

        dbClient.sql("insert into GROUP_USER (user_id, group_id) values (:user_id, :group_id)")
            .bind("user_id", user.id)
            .bind("group_id", groupId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }
}
