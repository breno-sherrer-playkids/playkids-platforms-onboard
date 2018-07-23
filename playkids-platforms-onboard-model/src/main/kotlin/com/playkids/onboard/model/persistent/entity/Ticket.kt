package com.playkids.onboard.model.persistent.entity

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

    var user
            by User.DAO referencedOn TicketTable.user

    var lottery
            by Lottery.DAO referencedOn TicketTable.lottery

    var buyDateTime
            by TicketTable.buyDateTime
}