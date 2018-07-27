package com.playkids.business.services

import com.nhaarman.mockito_kotlin.*
import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.business.event.EventLogger
import com.playkids.onboard.commons.ValidationException
import com.playkids.onboard.model.persistent.dao.UserDAO
import com.playkids.onboard.model.persistent.entity.User
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.math.BigDecimal
import kotlin.test.*

/**
 * Tests for the [UserService] module.
 */
class UserServiceTest : Spek({

    val mockEventLogger = mock<EventLogger>()
    val mockUserDAO = mock<UserDAO>()

    val mockUser = mock<User>()

    val userToken by lazy {
        doReturn("mail@mail.com")
                .whenever(mockUser).email

        UserSecurityToken(mockUser)
    }

    val userService = UserService(mockEventLogger, mockUserDAO)

    describe("an UserService") {

        beforeEachTest {
            reset(
                    mockEventLogger,
                    mockUserDAO,
                    mockUser
            )
        }

        on("creating an user") {

            it("should fail without an e-mail and password") {

                try {
                    runBlocking {
                        userService.createUser(ServerSecurityToken, null, null)
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.EMAIL_BLANK,
                                UserService.UserValidationIssue.PASSWORD_BLANK
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should fail without an e-mail") {
                try {
                    runBlocking {
                        userService.createUser(ServerSecurityToken, null, "randompass")
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.EMAIL_BLANK
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should fail without a password") {
                try {

                    // Mocks the dao find to return an empty iterable (because there's a validation we wanna skip)
                    whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                            .thenReturn(emptySized())

                    runBlocking {
                        userService.createUser(ServerSecurityToken, "validemail@google.com", null)
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.PASSWORD_BLANK
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should fail when password is too short") {
                try {

                    // Mocks the dao find to return an empty iterable (because there's a validation we wanna skip)
                    whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                            .thenReturn(emptySized())

                    runBlocking {
                        userService.createUser(ServerSecurityToken, "validemail@google.com", "123")
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.PASSWORD_TOO_SHORT
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should fail when an invalid email is supplied") {
                try {
                    runBlocking {
                        userService.createUser(ServerSecurityToken, "whatever@@", "randompass")
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.EMAIL_INVALID
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should fail with duplicate email") {
                try {

                    // Mocks the dao find to return an empty iterable (because there's a validation we wanna skip)
                    whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                            .thenReturn(SizedCollection(listOf(mockUser)))

                    runBlocking {
                        userService.createUser(ServerSecurityToken, "validemail@google.com", "randompass")
                    }

                    fail()
                } catch (dex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.EMAIL_DUPLICATED
                        ).containsAll(dex.issues)
                    }
                }
            }

            it("should create an user") {

                // Mocks the dao find to return an empty iterable (because there's a validation we wanna skip)
                whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                whenever(mockUserDAO.new(any())).thenReturn(mockUser)

                assertEquals(
                        mockUser,
                        runBlocking {
                            userService.createUser(ServerSecurityToken, "validemail@google.com", "randompass")
                        }
                )
            }
        }

        on("finding an user by email") {

            it("should return an user with the given email") {

                whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockUser)))

                assertNotNull(
                        runBlocking {
                            userService.findByEmail(ServerSecurityToken, "anyEmail")
                        }
                )
            }

            it("should return null as there's no user with the supplied email") {

                whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                assertNull(
                        runBlocking {
                            userService.findByEmail(ServerSecurityToken, "anyEmail")
                        }
                )
            }
        }

        on("finding an user by its credentials") {

            it("should return an user with the given credentials") {

                whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockUser)))

                assertNotNull(
                        runBlocking {
                            userService.findByCredentials(ServerSecurityToken, "anyEmail", "anyPassword")
                        }
                )
            }

            it("should return null as there's no user with the supplied credentials") {

                whenever(mockUserDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                assertNull(
                        runBlocking {
                            userService.findByCredentials(ServerSecurityToken, "anyEmail", "anyPassword")
                        }
                )
            }
        }

        on("buying credits") {

            it("should fail on invalid quantity") {

                try {
                    runBlocking {
                        userService.buyCredits(UserSecurityToken(mockUser), null)
                    }

                    fail()
                } catch (vex: ValidationException) {
                    assertTrue {
                        listOf(
                                UserService.UserValidationIssue.CREDITS_INVALID_QUANTITY
                        ).containsAll(vex.issues)
                    }
                }
            }

            it("should buy credits") {

                doReturn(BigDecimal.ZERO)
                        .whenever(mockUser).credits

                runBlocking {
                    userService.buyCredits(UserSecurityToken(mockUser), BigDecimal.TEN)
                }
            }
        }

        on("consuming the congratulations") {

            it("should return false as the user don't deserve congratulations") {

                doReturn(false)
                        .whenever(mockUser).congratulate

                assertFalse {
                    runBlocking {
                        userService.getAndConsumeCongratulations(userToken)
                    }
                }
            }

            it("should consume the congratulation") {

                doReturn(true)
                        .whenever(mockUser).congratulate

                assertTrue {
                    runBlocking {
                        userService.getAndConsumeCongratulations(userToken)
                    }
                }
            }
        }
    }
})
