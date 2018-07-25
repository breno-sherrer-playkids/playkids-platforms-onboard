package com.playkids.onboard.model.persistent.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.playkids.onboard.model.dto.LotteryDTO
import com.playkids.onboard.model.persistent.table.LotteryTable
import com.playkids.onboard.model.persistent.table.TicketTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

/**
 * "Lottery" Entity.
 */
class Lottery(ID: EntityID<Int>) : IntEntity(ID) {

    /**
     * DAO nested Singleton.
     */
    object DAO : IntEntityClass<Lottery>(LotteryTable)

    var status
            by LotteryTable.status

    var title
            by LotteryTable.title

    var prize
            by LotteryTable.prize

    var ticketPrice
            by LotteryTable.ticketPrice

    var winnerTicket
            by Ticket.DAO optionalReferencedOn LotteryTable.winnerTicket

    var lotteryDateTime
            by LotteryTable.lotteryDateTime

    /**
     * "many-to-one" back-reference.
     */
    @get:JsonIgnore
    val tickets
            by Ticket.DAO referrersOn TicketTable.lottery
}

/**
 * Transform a Lottery into a LotteryDTO.
 */
fun Lottery.dto() = LotteryDTO(this.id.value, this.title, this.prize, this.ticketPrice, this.lotteryDateTime)