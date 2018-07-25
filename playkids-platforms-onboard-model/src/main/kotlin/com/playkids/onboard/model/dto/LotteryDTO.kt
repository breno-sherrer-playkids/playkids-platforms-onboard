package com.playkids.onboard.model.dto

import org.joda.time.DateTime
import java.math.BigDecimal

/**
 * Defines a DTO representing a serializable Lottery.
 */
data class LotteryDTO(
        val ID: Int,
        val title: String,
        val prize: BigDecimal,
        val ticketPrice: BigDecimal,
        val raffleDateTime: DateTime)