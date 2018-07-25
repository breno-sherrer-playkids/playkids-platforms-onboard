package com.playkids.business.services

import com.playkids.business.auth.SecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.onboard.commons.DomainException
import com.playkids.onboard.commons.EntityNotFoundException
import com.playkids.onboard.commons.ValidationIssue
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.constants.LotteryConstants
import com.playkids.onboard.model.persistent.entity.Lottery
import com.playkids.onboard.model.persistent.entity.Ticket
import com.playkids.onboard.model.persistent.table.TicketTable
import org.joda.time.DateTime

/**
 * Provides functionalities related to the "Ticket" Entity.
 */
class TicketService(
        private val ticketDAO: Ticket.DAO,
        private val lotteryDAO: Lottery.DAO) {

    /**
     * Buy a Ticket.
     */
    suspend fun buyTicket(securityToken: SecurityToken, idLottery: Int?) {

        if (securityToken !is UserSecurityToken) {
            throw IllegalStateException("Trying to buy a ticket without an User Token")
        }

        if (idLottery == null) {
            throw DomainException(listOf(TicketValidationIssue.LOTTERY_NOT_SPECIFIED))
        }

        DatabaseConfigurator.transactionalContext {

            val lottery = lotteryDAO.findById(idLottery)
                    ?: throw EntityNotFoundException(Lottery::class, idLottery)

            // Besides the state, i have to check the lottery time because there's a gap in the state change and the
            // lottery raffle because of the timer task delay
            if (lottery.status != LotteryConstants.StatusConstants.PENDING
                    || !lottery.lotteryDateTime.isAfterNow) {

                throw DomainException(listOf(TicketValidationIssue.LOTTERY_INVALID))
            }

            val user = securityToken.user

            if (lottery.ticketPrice > user.credits) {
                throw DomainException(listOf(TicketValidationIssue.INSUFFICIENT_FUNDS))
            }

            user.credits = user.credits.subtract(lottery.ticketPrice)

            ticketDAO.new {
                this.user = user
                this.lottery = lottery
                this.buyDateTime = DateTime.now()
            }
        }
    }

    /**
     * Returns all Tickets owned by the authenticated User.
     */
    suspend fun findAllOwnedByUser(securityToken: SecurityToken): List<Ticket> =
            DatabaseConfigurator.transactionalContext {

                require(securityToken is UserSecurityToken) {
                    "Trying to find user tickets without an User Token"
                }

                val userToken = securityToken as UserSecurityToken

                ticketDAO.find {
                    TicketTable.user eq userToken.user.id
                }.toList()
            }

    /**
     * Enum defining the Validation issues for a Ticket Entity.
     */
    enum class TicketValidationIssue(
            override val title: String,
            override val description: String) : ValidationIssue {

        LOTTERY_NOT_SPECIFIED(
                "Failed to buy a Ticket",
                "Lottery ID must be specified"
        ),

        INSUFFICIENT_FUNDS(
                "Failed to buy a Ticket",
                "User has insufficient funds to finish the purchase"
        ),

        LOTTERY_INVALID(
                "Failed to buy a Ticket",
                "This Lottery is already closed"
        )
    }
}