package com.playkids.onboard.model.persistent.table

import com.playkids.onboard.model.persistent.constants.LotteryConstants
import org.jetbrains.exposed.dao.IntIdTable

/**
 * "Lottery" Table Definition.
 */
object LotteryTable : IntIdTable("lottery") {

    val status =
            enumeration("status", LotteryConstants.StatusConstants::class.java)
                    .default(LotteryConstants.StatusConstants.NOT_SPECIFIED)

    val title =
            varchar("title", 30)

    val prize =
            decimal("prize", 10, 4)

    val ticketPrice =
            decimal("ticketprice", 10, 4)

    val winnerTicket =
            reference("idwinnerticket", TicketTable)
                    .nullable()

    val lotteryDateTime =
            datetime("lotterydatetime")
}