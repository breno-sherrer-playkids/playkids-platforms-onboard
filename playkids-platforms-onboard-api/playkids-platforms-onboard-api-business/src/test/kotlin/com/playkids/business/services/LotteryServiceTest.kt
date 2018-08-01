package com.playkids.business.services

import com.nhaarman.mockito_kotlin.*
import com.playkids.business.assertExactValidationIssues
import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.business.event.EventLogger
import com.playkids.onboard.model.persistent.dao.LotteryDAO
import com.playkids.onboard.model.persistent.entity.Lottery
import com.playkids.onboard.model.persistent.entity.Ticket
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
import org.joda.time.DateTime
import java.math.BigDecimal
import kotlin.test.assertTrue

/**
 * Tests for the [LotteryService] module.
 */
class LotteryServiceTest : Spek({

    val mockEventLogger = mock<EventLogger>()
    val mockLotteryDAO = mock<LotteryDAO>()
    val mockEmailService = mock<EmailService>()

    val mockUser = mock<User>()
    val mockTicket = mock<Ticket>()
    val mockLottery = mock<Lottery>()

    val userToken = UserSecurityToken(mockUser)

    val lotteryService = LotteryService(mockEventLogger, mockLotteryDAO, mockEmailService)

    describe("a LotteryService") {

        beforeEachTest {
            reset(
                    mockEventLogger,
                    mockLotteryDAO,
                    mockUser,
                    mockTicket,
                    mockLottery,
                    mockEmailService
            )
        }

        on("creating a Lottery") {

            it("should fail when title isn't supplied") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.TITLE_BLANK) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = null,
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when title is too short (< 8)") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.TITLE_TOO_SHORT) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "123",
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when prize isn't supplied") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.PRIZE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = null,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when prize is zero") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.PRIZE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.ZERO,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when prize is negative") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.PRIZE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal(-5),
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when ticket price isn't supplied") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.TICKET_PRICE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.TEN,
                                ticketPrice = null,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when prize is zero") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.TICKET_PRICE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal.ZERO,
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when prize is negative") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.TICKET_PRICE_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal(-5),
                                lotteryDateTime = DateTime.now().plusDays(10))
                    }
                }
            }

            it("should fail when lottery date time isn't supplied") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.LOTTERY_DATETIME_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = null)
                    }
                }
            }

            it("should fail when lottery date time is past now") {

                assertExactValidationIssues(
                        LotteryService.LotteryValidationIssue.LOTTERY_DATETIME_INVALID) {

                    runBlocking {
                        lotteryService.createLottery(
                                securityToken = userToken,
                                title = "title123",
                                prize = BigDecimal.TEN,
                                ticketPrice = BigDecimal.TEN,
                                lotteryDateTime = DateTime.now().plusHours(-1))
                    }
                }
            }

            it("should create a Lottery") {

                runBlocking {
                    lotteryService.createLottery(
                            securityToken = userToken,
                            title = "title123",
                            prize = BigDecimal.TEN,
                            ticketPrice = BigDecimal.TEN,
                            lotteryDateTime = DateTime.now().plusHours(5))
                }
            }
        }

        on("getting all Pending Lotteries") {

            it("should return nothing as there's no Pending Lotteries") {

                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                assertTrue {
                    runBlocking {
                        lotteryService.getAllPending(userToken)
                    }.isEmpty()
                }
            }

            it("should return all Pending Lotteries") {

                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockLottery)))

                assertTrue {
                    runBlocking {
                        lotteryService.getAllPending(userToken)
                    }.isNotEmpty()
                }
            }
        }

        on("getting all Pending Lotteries to Raffle") {

            it("should return nothing as there's no Pending Lotteries to Raffle") {

                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                assertTrue {
                    runBlocking {
                        lotteryService.getAllPendingRaffle(userToken)
                    }.isEmpty()
                }
            }

            it("should return all Pending Lotteries to Raffle") {

                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockLottery)))

                assertTrue {
                    runBlocking {
                        lotteryService.getAllPendingRaffle(userToken)
                    }.isNotEmpty()
                }
            }
        }

        on("computing Lottery Winners") {

            it("should compute no winners as there's no Pending Lotteries to Raffle") {
                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                runBlocking {
                    lotteryService.computePendingLotteryWinners(ServerSecurityToken)
                }
            }

            it("should compute a winner") {
                whenever(mockLotteryDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockLottery)))

                doReturn(SizedCollection(listOf(mockTicket)))
                        .whenever(mockLottery).tickets

                doReturn(mockUser)
                        .whenever(mockTicket).user

                doReturn(BigDecimal.ZERO)
                        .whenever(mockUser).credits

                doReturn("loser@winner.com")
                        .whenever(mockUser).email

                doReturn(BigDecimal.TEN)
                        .whenever(mockLottery).prize

                doReturn("mocked lottery")
                        .whenever(mockLottery).title

                runBlocking {
                    lotteryService.computePendingLotteryWinners(ServerSecurityToken)
                }
            }
        }
    }
})