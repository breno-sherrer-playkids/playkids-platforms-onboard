package com.playkids.business.services

import com.nhaarman.mockito_kotlin.*
import com.playkids.business.auth.UserSecurityToken
import com.playkids.business.event.EventLogger
import com.playkids.onboard.commons.EntityNotFoundException
import com.playkids.onboard.commons.ValidationException
import com.playkids.onboard.model.persistent.constants.LotteryConstants
import com.playkids.onboard.model.persistent.dao.LotteryDAO
import com.playkids.onboard.model.persistent.dao.TicketDAO
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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Tests for the [TicketService] module.
 */
class TicketServiceTest : Spek({

    val mockEventLogger = mock<EventLogger>()
    val mockTicketDAO = mock<TicketDAO>()
    val mockLotteryDAO = mock<LotteryDAO>()

    val mockUser = mock<User>()
    val mockTicket = mock<Ticket>()
    val mockLottery = mock<Lottery>()

    val userToken = UserSecurityToken(mockUser)

    val ticketService = TicketService(mockEventLogger, mockTicketDAO, mockLotteryDAO)

    describe("a TicketService") {

        beforeEachTest {
            reset(
                    mockEventLogger,
                    mockTicketDAO,
                    mockLotteryDAO,
                    mockUser,
                    mockTicket
            )
        }

        on("finding all owned by User") {

            it("should return an empty list when the User has no tickets") {

                whenever(mockTicketDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(emptySized())

                assertTrue {
                    runBlocking {
                        ticketService.findAllOwnedByUser(userToken)
                    }.isEmpty()
                }
            }

            it("should return an list with all tickets") {

                whenever(mockTicketDAO.find(any<SqlExpressionBuilder.() -> Op<Boolean>>()))
                        .thenReturn(SizedCollection(listOf(mockTicket)))

                assertTrue {
                    runBlocking {
                        ticketService.findAllOwnedByUser(userToken)
                    }.isNotEmpty()
                }
            }
        }

        on("buying a Ticket") {

            it("should fail when the lottery ID isn't supplied") {

                try {
                    runBlocking {
                        ticketService.buyTicket(userToken, null)
                    }

                    fail()
                } catch (vex: ValidationException) {
                    assertTrue {
                        listOf(
                                TicketService.TicketValidationIssue.LOTTERY_NOT_SPECIFIED
                        ).containsAll(vex.issues)
                    }
                }
            }

            it("should fail when the lottery ID is invalid") {

                whenever(mockLotteryDAO.findById(any<Int>()))
                        .thenReturn(null)

                assertFailsWith(EntityNotFoundException::class) {
                    runBlocking {
                        ticketService.buyTicket(userToken, -4100)
                    }
                }

            }

            it("should fail when the lottery is already finished") {

                whenever(mockLotteryDAO.findById(any<Int>()))
                        .thenReturn(mockLottery)

                doReturn(LotteryConstants.StatusConstants.FINALIZED)
                        .whenever(mockLottery).status

                try {
                    runBlocking {
                        ticketService.buyTicket(userToken, 123)
                    }

                    fail()
                } catch (vex: ValidationException) {
                    assertTrue {
                        listOf(
                                TicketService.TicketValidationIssue.LOTTERY_INVALID
                        ).containsAll(vex.issues)
                    }
                }
            }

            it("should fail when the lottery is expired") {

                whenever(mockLotteryDAO.findById(any<Int>()))
                        .thenReturn(mockLottery)

                doReturn(LotteryConstants.StatusConstants.PENDING)
                        .whenever(mockLottery).status

                doReturn(DateTime.now().plusHours(-2))
                        .whenever(mockLottery).lotteryDateTime

                try {
                    runBlocking {
                        ticketService.buyTicket(userToken, 123)
                    }

                    fail()
                } catch (vex: ValidationException) {
                    assertTrue {
                        listOf(
                                TicketService.TicketValidationIssue.LOTTERY_INVALID
                        ).containsAll(vex.issues)
                    }
                }
            }

            it("should fail when the user has insufficient credits") {

                whenever(mockLotteryDAO.findById(any<Int>()))
                        .thenReturn(mockLottery)

                doReturn(LotteryConstants.StatusConstants.PENDING)
                        .whenever(mockLottery).status

                doReturn(DateTime.now().plusHours(10))
                        .whenever(mockLottery).lotteryDateTime

                doReturn(BigDecimal.TEN)
                        .whenever(mockLottery).ticketPrice

                doReturn(BigDecimal.ZERO)
                        .whenever(mockUser).credits

                try {
                    runBlocking {
                        ticketService.buyTicket(userToken, 123)
                    }

                    fail()
                } catch (vex: ValidationException) {
                    assertTrue {
                        listOf(
                                TicketService.TicketValidationIssue.INSUFFICIENT_FUNDS
                        ).containsAll(vex.issues)
                    }
                }
            }

            it("should buy the ticket") {

                whenever(mockLotteryDAO.findById(any<Int>()))
                        .thenReturn(mockLottery)

                whenever(mockTicketDAO.new(any()))
                        .thenReturn(mockTicket)

                doReturn(LotteryConstants.StatusConstants.PENDING)
                        .whenever(mockLottery).status

                doReturn(DateTime.now().plusHours(10))
                        .whenever(mockLottery).lotteryDateTime

                doReturn(BigDecimal.ONE)
                        .whenever(mockLottery).ticketPrice

                doReturn(BigDecimal.TEN)
                        .whenever(mockUser).credits

                runBlocking {
                    ticketService.buyTicket(userToken, 123)
                }
            }
        }
    }
})