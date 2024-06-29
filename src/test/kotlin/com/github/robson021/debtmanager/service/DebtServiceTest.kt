package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.getTestUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
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

    private val testUser = getTestUser()

    @BeforeEach
    fun setup() {
        runBlocking {
            userService.addGoogleUserIfNotPresent(testUser)
        }

    }

    @Test
    fun `creates new group with its owner`() = runBlocking {
        debtService.createNewGroup(testUser, "Test Group")
    }

    @Test
    fun `fail to create new group with duplicated name and owner`(): Unit = runBlocking {
        debtService.createNewGroup(testUser, "Duplicated Group")

        assertThrows<DuplicateKeyException> {
            debtService.createNewGroup(testUser, "Duplicated Group")
        }
    }

}
