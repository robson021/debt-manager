package com.github.robson021.debtmanager.service

import com.github.robson021.debtmanager.getTestUser
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    private val googleUser = getTestUser()

    @Test
    fun `saves new user once and then finds it`(): Unit = runBlocking {
        userService.addGoogleUserIfNotPresent(googleUser)
        userService.addGoogleUserIfNotPresent(googleUser)
        userService.addGoogleUserIfNotPresent(googleUser)

        val u = userService.findUserBySub(googleUser.sub)

        assertThat(u.sub).isEqualTo(googleUser.sub)
        assertThat(u.name).isEqualTo(googleUser.name)
        assertThat(u.email).isEqualTo(googleUser.email)
    }
}
