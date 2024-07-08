package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.addUsersBatch
import com.github.robson021.debtmanager.getTestUser
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
import java.math.BigDecimal

@SpringBootTest
class DebtServiceTest {

    @Autowired
    private lateinit var debtService: DebtService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `creates new group with its owner`(): Unit = runBlocking {
        val testUser = getTestUser("111")
        userService.addGoogleUserIfNotPresent(testUser)

        userService.addGoogleUserIfNotPresent(testUser)
        debtService.createNewGroup(testUser, "Test Group")
    }

    @Test
    fun `fail to create new group with duplicated name and owner`(): Unit = runBlocking {
        val testUser = getTestUser("222")
        userService.addGoogleUserIfNotPresent(testUser)

        debtService.createNewGroup(testUser, "Duplicated Group")

        assertThrows<DuplicateKeyException> {
            debtService.createNewGroup(testUser, "Duplicated Group")
        }
    }

    @Test
    fun `finds a single group of user`(): Unit = runBlocking {
        val testUser = getTestUser("333")
        userService.addGoogleUserIfNotPresent(testUser)

        val groupName = "Some Group 1"
        debtService.createNewGroup(testUser, groupName)

        val groups = debtService.listUserGroups(testUser)

        assertThat(groups).hasSize(1)
        assertThat(groups[0].name).isEqualTo(groupName)
    }

    @Test
    fun `finds all groups that user owns`(): Unit = runBlocking {
        val testUser = getTestUser("444")
        userService.addGoogleUserIfNotPresent(testUser)

        debtService.createNewGroup(testUser, "Test Group 1")
        debtService.createNewGroup(testUser, "Test Group 2")
        debtService.createNewGroup(testUser, "Test Group 3")

        val groups = debtService.listUserGroups(testUser)
        assertThat(groups).hasSize(3)

        assertThat(groups[0].name).isEqualTo("Test Group 1")
        assertThat(groups[1].name).isEqualTo("Test Group 2")
        assertThat(groups[2].name).isEqualTo("Test Group 3")
    }

    @Test
    fun `add users to existing group`(): Unit = runBlocking {
        val testUser1 = getTestUser("aaa")
        val testUser2 = getTestUser("bbb")
        val testUser3 = getTestUser("ccc")
        userService.addGoogleUserIfNotPresent(testUser1)
        userService.addGoogleUserIfNotPresent(testUser2)
        userService.addGoogleUserIfNotPresent(testUser3)

        val groupID = debtService.createNewGroup(testUser1, "Test Group With Multiple Users")

        debtService.addUserToGroup(testUser2, groupID)
        debtService.addUserToGroup(testUser3, groupID)

        val users = debtService.listAllUsersInGroup(groupID)
        assertThat(users).hasSize(3)
    }

    @Test
    fun `add debt to other user`(): Unit = runBlocking {
        // given
        val lender = getTestUser("lender")
        val borrower = getTestUser("borrower")
        userService.addUsersBatch(lender, borrower)

        val groupID = debtService.createNewGroup(lender, "Borrowers and Lenders")
        debtService.addUserToGroup(borrower, groupID)

        val borrowerID = userService.findUserBySub(borrower.sub).id
        val debt = BigDecimal("123.98")

        // when
        debtService.addDebt(lender, borrowerID, groupID, debt)

        // then
        val allDebts = debtService.finUserDebts(borrower)
        assertThat(allDebts).hasSize(1)
        assertThat(allDebts[0].amount).isEqualTo(debt)

        val balance1 = debtService.getUserBalance(lender)
        val balance2 = debtService.getUserBalance(borrower)

        assertThat(balance1).isEqualTo(debt)
        assertThat(balance2).isEqualTo(debt.negate())
    }

    @Test
    fun `add new user to existing group using invitation code`(): Unit = runBlocking {
        val owner = getTestUser("owner")
        val newUser = getTestUser("newcomer")
        userService.addUsersBatch(owner, newUser)

        val groupID = debtService.createNewGroup(owner, "Group with invitation code")
        val ownerID = userService.findUserBySub(owner.sub).id
        val invitationCode = debtService.listUserGroups(owner).first().invitationCode

        debtService.addUserToGroupWithCodeValidation(newUser, ownerID, invitationCode)

        val users = debtService.listAllUsersInGroup(groupID)
        assertThat(users).hasSize(2)
        assertThat(users.map { it.email }).containsExactlyInAnyOrder(owner.email, newUser.email)
    }

}
