package com.playkids.business.event

/**
 * Maps all the possible Events to the respective 'type' representation in the database.
 */
enum class Event(
        val type: String) {

    EMAIL_SENDING(
            "email_sending"
    ),

    LOTTERY_CREATION(
            "lottery_creation"
    ),

    LOTTERY_RAFFLE(
            "lottery_raffle"
    ),

    TICKET_BUYING(
            "ticket_buying"
    ),

    USER_CREATION(
            "user_creation"
    ),

    USER_CREDIT_PURCHASE(
            "user_credit_purchase"
    ),

    USER_CONGRATULATION(
            "user_congratulation"
    )
}