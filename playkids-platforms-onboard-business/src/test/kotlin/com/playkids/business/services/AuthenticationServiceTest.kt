package com.playkids.business.services

import com.nhaarman.mockito_kotlin.*
import com.playkids.business.auth.UserSecurityToken
import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.UserService
import com.playkids.onboard.model.persistent.entity.User
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the [AuthenticationService] module.
 */
class AuthenticationServiceTest : Spek({

    val mockUserService = mock<UserService>()

    val mockUser = mock<User>()

    val authenticationService = AuthenticationService(mockUserService)

    describe("an AuthenticationService") {

        beforeEachTest {
            reset(
                    mockUserService,
                    mockUser
            )
        }

        on("generating user security token") {

            it("should return a null token for an invalid JWT token") {
                runBlocking {
                    assertNull(authenticationService.generateUserSecurityToken("anything.but.a.JWT.token.:)"))
                }
            }

            it("should create a valid token for the given JWT token") {

                doReturn("fakeuser@mail.com")
                        .whenever(mockUser).email

                whenever(runBlocking { mockUserService.findByEmail(any(), any()) })
                        .thenReturn(mockUser)

                val jwtToken = authenticationService.generateToken(mockUser)

                runBlocking {

                    val generatedToken = authenticationService.generateUserSecurityToken(jwtToken)

                    assertNotNull(generatedToken)

                    assertTrue { generatedToken is UserSecurityToken }

                    assertEquals(mockUser, (generatedToken as UserSecurityToken).user)
                }
            }
        }

        on("generating a JWT token for an User") {

            it("should generate a valid JWT token") {
                doReturn("fakeuser@mail.com")
                        .whenever(mockUser).email

                authenticationService.generateToken(mockUser)
            }
        }
    }
})