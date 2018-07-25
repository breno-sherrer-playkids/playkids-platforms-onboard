package com.playkids.onboard.model.persistent.table

import org.jetbrains.exposed.dao.IntIdTable

/**
 * "EventLog" Table Definition.
 */
object EventLogTable : IntIdTable("eventlog") {

    val type =
            varchar("type", 30)

    val user =
            reference("iduser", UserTable)

    val eventDateTime =
            datetime("eventdatetime")
}