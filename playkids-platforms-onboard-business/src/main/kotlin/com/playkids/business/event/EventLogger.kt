package com.playkids.business.event

import com.playkids.business.auth.SecurityToken

/**
 * Defines an Interface for Event Logging.
 */
interface EventLogger {

    /**
     * Logs an Event.
     */
    suspend fun log(securityToken: SecurityToken, event: Event): Unit
}