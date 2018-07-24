package com.playkids.business.services

import org.joda.time.DateTime
import java.math.BigDecimal

/**
 * Provides functionalities related to the "Lottery" Entity.
 */
class LotteryService {

    fun createLotery(token: String, title: String, prize: BigDecimal, ticketPrice: BigDecimal, lotteryDateTime: DateTime) {
        TODO()
    }

    fun computePendingLotteryWinners(token: String) {
        TODO()
    }
}