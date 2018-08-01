package com.playkids.business.event

import com.playkids.business.auth.SecurityToken
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.entity.EventLog
import org.joda.time.DateTime

/**
 * Responsible to Log every requested event.
 */
class DatabaseEventLogger(
        private val eventLogDAO: EventLog.DAO) : EventLogger {

    /**
     * Logs an Event.
     */
    override suspend fun log(securityToken: SecurityToken, event: Event): Unit =
            DatabaseConfigurator.transactionalContext {

                eventLogDAO.new {
                    this.author = securityToken.principal
                    this.type = event.type
                    this.eventDateTime = DateTime.now()
                }
            }
}