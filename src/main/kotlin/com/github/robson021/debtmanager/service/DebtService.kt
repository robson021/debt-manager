package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.extensions.GoogleUserDetails
import com.github.robson021.debtmanager.logger
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Service
@Transactional
class DebtService(
    private val dbClient: DatabaseClient,
) {

    suspend fun createNewGroup(owner: GoogleUserDetails, groupName: String) {
        if (!StringUtils.hasText(groupName) || groupName.length < 3) {
            throw RuntimeException("Group name must be at least 3 characters")
        }

        val userID = getUserBySub(owner.sub)
        createNewGroup(groupName, userID)

        val groupId = getGroupID(userID, groupName)
        addUserToGroup(userID, groupId)

        log.info("New group created: $groupName. Owner: ${owner.toShortString()}.")
    }

    suspend fun listMyGroups(user: GoogleUserDetails) {
        // TODO
    }

    private suspend fun createNewGroup(groupName: String, userID: Int) {
        dbClient.sql("insert into GROUPS (name, owner_id) values (:name, :owner_id)")
            .bind("name", groupName)
            .bind("owner_id", userID)
            .fetch()
            .awaitRowsUpdated()
    }

    private suspend fun getGroupID(ownerID: Int, groupName: String) =
        dbClient.sql("select id from GROUPS g where g.owner_id = :owner_id and g.name = :name")
            .bind("owner_id", ownerID)
            .bind("name", groupName)
            .fetch()
            .first()
            .awaitSingle()["id"] as Int

    private suspend fun getUserBySub(sub: String) = dbClient.sql("select id from USERS u where u.sub = :sub")
        .bind("sub", sub)
        .fetch()
        .first()
        .awaitSingle()["id"] as Int

    private suspend fun addUserToGroup(userID: Int, groupId: Int) {
        dbClient.sql("insert into GROUP_USER (user_id, group_id) values (:user_id, :group_id)")
            .bind("user_id", userID)
            .bind("group_id", groupId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    companion object {
        private val log by logger()
    }

}
