package com.playkids.onboard.model.persistent.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.playkids.onboard.model.dto.TicketDTO
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.table.TicketTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

/**
 * "Ticket" Entity.
 */
class Ticket(ID: EntityID<Int>) : IntEntity(ID) {

    /**
     * DAO nested Singleton.
     */
    object DAO : IntEntityClass<Ticket>(TicketTable)

    @get:JsonIgnore
    var user
            by User.DAO referencedOn TicketTable.user

    @get:JsonIgnore
    var lottery
            by Lottery.DAO referencedOn TicketTable.lottery

    var buyDateTime
            by TicketTable.buyDateTime
}

/**
 * Transforms a Ticket into a TicketDTO.
 */
suspend fun Ticket.dto() = DatabaseConfigurator.transactionalContext {
    TicketDTO(this.id.value, this.lottery.title, this.buyDateTime)
}
