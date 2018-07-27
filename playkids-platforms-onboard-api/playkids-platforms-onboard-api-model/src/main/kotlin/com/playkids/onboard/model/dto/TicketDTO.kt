package com.playkids.onboard.model.dto

import org.joda.time.DateTime

/**
 * Defines a DTO representing a Ticket.
 */
data class TicketDTO(
        val ID: Int,
        val lottery: String,
        val buyDateTime: DateTime)