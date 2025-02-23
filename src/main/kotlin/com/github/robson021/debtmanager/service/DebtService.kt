package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.db.Debt
import com.github.robson021.debtmanager.db.Group
import com.github.robson021.debtmanager.db.User
import com.github.robson021.debtmanager.debug
import com.github.robson021.debtmanager.extension.GoogleUserDetails
import com.github.robson021.debtmanager.logger
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

@Service
@Transactional
class DebtService(
    private val dbClient: DatabaseClient,
    private val userService: UserService,
) {

    suspend fun createNewGroup(owner: GoogleUserDetails, groupName: String): Int {
        if (!StringUtils.hasText(groupName) || groupName.length < 3) {
            throw RuntimeException("Group name must be at least 3 characters")
        }

        val userID = userService.getUserId(owner.sub)
        createNewGroup(groupName, userID)

        val groupId = getGroupID(userID, groupName)
        addUserToGroup(userID, groupId)

        return groupId
    }

    suspend fun addUserToGroupWithCodeValidation(user: GoogleUserDetails, ownerID: Int, invitationCode: String) {
        val groupId = dbClient.sql("select id from GROUPS g where g.owner_id = :owner_id and g.invitation_code = :invitation_code")
            .bind("owner_id", ownerID)
            .bind("invitation_code", invitationCode)
            .fetch()
            .awaitSingle()["id"] as Int
        log.debug { "User '${user.toShortString()}' has passed validation of joining group with id $groupId." }
        addUserToGroup(userService.getUserId(user.sub), groupId)
    }

    suspend fun addUserToGroup(user: GoogleUserDetails, groupID: Int) = addUserToGroup(userService.getUserId(user.sub), groupID)

    private suspend fun addUserToGroup(userID: Int, groupId: Int) {
        dbClient.sql("insert into GROUP_USER (user_id, group_id) values (:user_id, :group_id)")
            .bind("user_id", userID)
            .bind("group_id", groupId)
            .fetch()
            .awaitRowsUpdated()
        log.debug { "Added user $userID to group $groupId" }
    }

    suspend fun listUserGroups(user: GoogleUserDetails): List<Group> =
        dbClient.sql("select * from GROUPS g inner join GROUP_USER gu on gu.user_id = :userId and gu.group_id = g.id order by g.name")
            .bind("userId", userService.getUserId(user.sub))
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

    suspend fun getUserBalance(user: GoogleUserDetails): BigDecimal {
        val userID = userService.getUserId(user.sub)
        val whereLender = "(coalesce ((select sum (amount) from DEBTS d where d.lender_id = :lender_id), 0))"
        val whereBorrower = whereLender.replace("lender_id", "borrower_id")
        return dbClient.sql("select ($whereLender - $whereBorrower) as diff")
            .bind("lender_id", userID)
            .bind("borrower_id", userID)
            .fetch()
            .awaitSingle()["diff"] as BigDecimal
    }

    suspend fun addSplitPaymentDebt(lender: GoogleUserDetails, groupID: Int, debt: BigDecimal, vararg borrowerIDs: Int) {
        val toSplit = borrowerIDs.size + 1
        borrowerIDs.forEach { addDebt(lender, it, groupID, debt.divide(toSplit.toBigDecimal(), MathContext.DECIMAL32)) }
    }

    suspend fun addDebt(lender: GoogleUserDetails, borrowerID: Int, groupID: Int, debt: BigDecimal) {
        val lenderID = userService.getUserId(lender.sub)
        assertBothUsersAreInTheSameGroup(lenderID, borrowerID, groupID)

        dbClient.sql("insert into DEBTS (amount, lender_id, borrower_id, group_id) values (:amount, :lender_id, :borrower_id, :group_id)")
            .bind("amount", debt)
            .bind("lender_id", lenderID)
            .bind("borrower_id", borrowerID)
            .bind("group_id", groupID)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun finUserDebts(user: GoogleUserDetails) =
        dbClient.sql("select * from DEBTS d where d.borrower_id = (select id from USERS u where u.sub = :sub)")
            .bind("sub", user.sub)
            .mapProperties(Debt::class.java)
            .all()
            .asFlow()
            .toList()

    private suspend fun createNewGroup(groupName: String, userID: Int): String {
        val code = UUID.randomUUID().toString()
        dbClient.sql("insert into GROUPS (name, owner_id, invitation_code) values (:name, :owner_id, :invitation_code)")
            .bind("name", groupName)
            .bind("owner_id", userID)
            .bind("invitation_code", code)
            .fetch()
            .awaitRowsUpdated()
        log.debug { "New group created: $groupName. Owner: $userID. Invitation code: $code" }
        return code
    }

    private suspend fun getGroupID(ownerID: Int, groupName: String) =
        dbClient.sql("select id from GROUPS g where g.owner_id = :owner_id and g.name = :name")
            .bind("owner_id", ownerID)
            .bind("name", groupName)
            .fetch()
            .first()
            .awaitSingle()["id"] as Int

    private suspend fun assertBothUsersAreInTheSameGroup(lenderID: Int, borrowerID: Int, groupID: Int) {
        val sql = buildString {
            append("(select 1 from GROUP_USER gu where gu.user_id = :lenderID and gu.group_id = :group_id)")
            append(" union ")
            append("(select 2 from GROUP_USER gu where gu.user_id = :borrowerID and gu.group_id = :group_id)")
        }
        val rows = dbClient.sql(sql)
            .bind("group_id", groupID)
            .bind("lenderID", lenderID)
            .bind("borrowerID", borrowerID)
            .fetch()
            .all()
            .asFlow()
            .count()

        if (rows != 2) {
            throw RuntimeException("Users are not in the same group")
        }
    }

    companion object {
        private val log by logger()
    }

}
