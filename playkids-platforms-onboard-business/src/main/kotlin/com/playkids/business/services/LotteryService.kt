package com.playkids.business.services

import com.playkids.business.auth.SecurityToken
import com.playkids.business.auth.ServerSecurityToken
import com.playkids.onboard.commons.DomainException
import com.playkids.onboard.commons.ValidationIssue
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.constants.LotteryConstants
import com.playkids.onboard.model.persistent.entity.Lottery
import com.playkids.onboard.model.persistent.table.LotteryTable
import io.netty.util.internal.logging.InternalLoggerFactory
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime
import java.math.BigDecimal
import java.util.*

/**
 * Provides functionalities related to the "Lottery" Entity.
 */
class LotteryService(
        private val lotteryDAO: Lottery.DAO,
        private val emailService: EmailService) {

    /**
     * Creates a Lottery.
     */
    suspend fun createLottery(securityToken: SecurityToken, title: String?, prize: BigDecimal?,
                              ticketPrice: BigDecimal?, lotteryDateTime: DateTime?) {

        val issues = mutableListOf<ValidationIssue>()

        if (title.isNullOrBlank()) {
            issues.add(LotteryValidationIssue.TITLE_BLANK)

        } else if (title!!.length < 8) {
            issues.add(LotteryValidationIssue.TITLE_BLANK)

        }

        if (prize == null || prize <= BigDecimal.ZERO) {
            issues.add(LotteryValidationIssue.PRIZE_INVALID)
        }

        if (ticketPrice == null || ticketPrice <= BigDecimal.ZERO) {
            issues.add(LotteryValidationIssue.TICKET_PRICE_INVALID)
        }

        if (lotteryDateTime == null || lotteryDateTime.isBeforeNow) {
            issues.add(LotteryValidationIssue.LOTTERYDATETIME_INVALID)
        }

        if (issues.isNotEmpty()) {
            throw DomainException(issues)
        }

        DatabaseConfigurator.transactionalContext {
            lotteryDAO.new {
                this.status = LotteryConstants.StatusConstants.PENDING
                this.title = title!!
                this.prize = prize!!
                this.ticketPrice = ticketPrice!!
                this.lotteryDateTime = lotteryDateTime!!
            }
        }
    }

    /**
     * Return all active Lotteries.
     */
    suspend fun getAllPending(securityToken: SecurityToken) =
            DatabaseConfigurator.transactionalContext {

                lotteryDAO.find {
                    LotteryTable.status eq LotteryConstants.StatusConstants.PENDING
                }.toList()
            }

    /**
     * Return all Lotteries whose Raffle is pending.
     */
    private suspend fun getAllPendingRaffle(securityToken: SecurityToken) =
            DatabaseConfigurator.transactionalContext {

                lotteryDAO.find {
                    LotteryTable.status eq LotteryConstants.StatusConstants.PENDING and
                            (LotteryTable.lotteryDateTime lessEq DateTime.now())
                }.toList()
            }

    /**
     * Computes all the Pending Lottery and Raffle a winner.
     */
    suspend fun computePendingLotteryWinners(securityToken: SecurityToken) {

        require(securityToken === ServerSecurityToken) {
            "Trying to compute Pending Lotteries without server token"
        }

        val pendingLotteries = getAllPendingRaffle(ServerSecurityToken)

        pendingLotteries.forEach {

            DatabaseConfigurator.transactionalContext {

                val tickets = it.tickets.toList()

                val winnerTicket = tickets[Random().nextInt(tickets.size)]

                val winnerUser = winnerTicket.user

                InternalLoggerFactory
                        .getInstance(this::class.java)
                        .info("Raffle: Lottery '${it.title}' winner: ${winnerUser.email}")

                it.winnerTicket = winnerTicket
                it.status = LotteryConstants.StatusConstants.FINALIZED

                winnerUser.credits = winnerUser.credits.add(it.prize)
                winnerUser.congratulate = true

                val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    println("Failed to send E-Mail: ${throwable.localizedMessage}")
                }

                launch(coroutineExceptionHandler) {

                    emailService.sendEmail(
                            winnerUser.email,
                            "PlayKids Lottery - Congratulations!",
                            "<html><h1>You won ${it.prize}!!!</h1></html>")
                }
            }
        }
    }

    /**
     * Enum definition of the Lottery Validation Issues.
     */
    enum class LotteryValidationIssue(
            override val title: String,
            override val description: String) : ValidationIssue {

        TITLE_BLANK(
                "Failed to create a Lottery",
                "Title must be specified"
        ),

        TITLE_TOO_SHORT(
                "Failed to create a Lottery",
                "Title must have at least 8 characters"
        ),

        PRIZE_INVALID(
                "Failed to create a Lottery",
                "Prize must be greater than Zero"
        ),

        TICKET_PRICE_INVALID(
                "Failed to create a Lottery",
                "Ticket Price must be greater than Zero"
        ),

        LOTTERYDATETIME_INVALID(
                "Failed to create a Lottery",
                "Lottery Date Time must be greater than the current date time"
        )
    }
}