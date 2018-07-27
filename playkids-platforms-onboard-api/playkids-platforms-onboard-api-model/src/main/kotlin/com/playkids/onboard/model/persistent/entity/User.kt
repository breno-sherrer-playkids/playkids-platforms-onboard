package com.playkids.onboard.model.persistent.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.playkids.onboard.model.persistent.dao.UserDAO
import com.playkids.onboard.model.persistent.table.TicketTable
import com.playkids.onboard.model.persistent.table.UserTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

/**
 * "User" Entity.
 */
class User(ID: EntityID<Int>) : IntEntity(ID) {

    /**
     * DAO nested Singleton.
     */
    object DAO : UserDAO()

    var email
            by UserTable.email

    var password
            by UserTable.password

    var credits
            by UserTable.credits

    var congratulate
            by UserTable.congratulate

    /**
     * "many-to-one" back-reference.
     */
    @get:JsonIgnore
    val tickets
            by Ticket.DAO referrersOn TicketTable.user
}