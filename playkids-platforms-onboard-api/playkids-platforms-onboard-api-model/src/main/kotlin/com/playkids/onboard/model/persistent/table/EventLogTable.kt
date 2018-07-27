package com.playkids.onboard.model.persistent.table

import org.jetbrains.exposed.dao.IntIdTable

/**
 * "EventLog" Table Definition.
 */
object EventLogTable : IntIdTable("eventlog") {

    val type =
            varchar("type", 30)

    val author =
            varchar("author", 80)

    val eventDateTime =
            datetime("eventdatetime")
}