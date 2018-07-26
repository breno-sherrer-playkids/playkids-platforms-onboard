package com.playkids.onboard.model.persistent.table

import org.jetbrains.exposed.dao.IntIdTable

/**
 * "Ticket" Table Definition.
 */
object TicketTable : IntIdTable("ticket") {

    val user =
            reference("iduser", UserTable)

    val lottery =
            reference("idlottery", LotteryTable)

    val buyDateTime =
            datetime("buydatetime")
}