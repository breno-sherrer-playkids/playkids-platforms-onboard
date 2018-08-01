package com.playkids.onboard.model.persistent.constants

/**
 * Constants for the Lottery Entity.
 */
object LotteryConstants {

    /**
     * "Status" constants.
     */
    enum class StatusConstants(value: Int) {
        NOT_SPECIFIED(0),
        PENDING(1),
        CANCELED(2),
        FINALIZED(3);
    }
}
