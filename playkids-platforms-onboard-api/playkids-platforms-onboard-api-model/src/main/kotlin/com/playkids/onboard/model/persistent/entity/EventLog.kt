package com.playkids.onboard.model.persistent.entity

import com.playkids.onboard.model.persistent.dao.EventLogDAO
import com.playkids.onboard.model.persistent.table.EventLogTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

/**
 * "EventLog" Entity.
 */
class EventLog(ID: EntityID<Int>) : IntEntity(ID) {

    /**
     * DAO nested Singleton.
     */
    object DAO : EventLogDAO()

    var type
            by EventLogTable.type

    var author
            by EventLogTable.author

    var eventDateTime
            by EventLogTable.eventDateTime
}