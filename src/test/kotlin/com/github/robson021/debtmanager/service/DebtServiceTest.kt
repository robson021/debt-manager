package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.getTestUser
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException

@SpringBootTest
class DebtServiceTest {

    @Autowired
    private lateinit var debtService: DebtService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `creates new group with its owner`() = runBlocking {
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

}
