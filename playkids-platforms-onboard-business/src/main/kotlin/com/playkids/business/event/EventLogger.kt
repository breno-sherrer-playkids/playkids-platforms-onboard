package com.playkids.business.event

import com.playkids.business.auth.SecurityToken
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.entity.EventLog
import org.joda.time.DateTime

/**
 * Responsible to Log every requested event.
 */
class EventLogger(
        private val eventLogDAO: EventLog.DAO) {

    /**
     * Logs an Event.
     */
    suspend fun log(securityToken: SecurityToken, event: Event) =
            DatabaseConfigurator.transactionalContext {

                eventLogDAO.new {
                    this.author = securityToken.principal
                    this.type = event.type
                    this.eventDateTime = DateTime.now()
                }
            }
}