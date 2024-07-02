package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.Group
import com.github.robson021.debtmanager.db.User
import com.github.robson021.debtmanager.extensions.GoogleUserDetails
import com.github.robson021.debtmanager.logger
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
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

    suspend fun createNewGroup(owner: GoogleUserDetails, groupName: String): Int {
        if (!StringUtils.hasText(groupName) || groupName.length < 3) {
            throw RuntimeException("Group name must be at least 3 characters")
        }

        val userID = getUserIdBySub(owner.sub)
        createNewGroup(groupName, userID)

        val groupId = getGroupID(userID, groupName)
        addUserToGroup(userID, groupId)

        return groupId
    }

    suspend fun addUserToGroup(user: GoogleUserDetails, groupID: Int) {
        val userID = getUserIdBySub(user.sub)
        addUserToGroup(userID, groupID)
    }

    suspend fun listUserGroups(user: GoogleUserDetails): List<Group> =
        dbClient.sql("select * from GROUPS g inner join GROUP_USER gu on gu.user_id = :userId and gu.group_id = g.id order by g.name")
            .bind("userId", getUserIdBySub(user.sub))
            .mapProperties(Group::class.java)
            .all()
            .asFlow()
            .toList()

    suspend fun listAllUsersInGroup(groupID: Int): List<User> {
        return dbClient.sql("select * from USERS u inner join GROUP_USER gu on u.id = gu.user_id where gu.group_id = :groupId")
            .bind("groupId", groupID)
            .mapProperties(User::class.java)
            .all()
            .asFlow()
            .toList()
    }

    private suspend fun getUserIdBySub(sub: String) = dbClient.sql("select id from USERS u where u.sub = :sub")
        .bind("sub", sub)
        .fetch()
        .first()
        .awaitSingle()["id"] as Int

    private suspend fun createNewGroup(groupName: String, userID: Int) {
        dbClient.sql("insert into GROUPS (name, owner_id) values (:name, :owner_id)")
            .bind("name", groupName)
            .bind("owner_id", userID)
            .fetch()
            .awaitRowsUpdated()
        log.debug("New group created: $groupName. Owner: $userID.")
    }

    private suspend fun getGroupID(ownerID: Int, groupName: String) =
        dbClient.sql("select id from GROUPS g where g.owner_id = :owner_id and g.name = :name")
            .bind("owner_id", ownerID)
            .bind("name", groupName)
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
        log.debug("Added user $userID to group $groupId")
    }

    companion object {
        private val log by logger()
    }

}
