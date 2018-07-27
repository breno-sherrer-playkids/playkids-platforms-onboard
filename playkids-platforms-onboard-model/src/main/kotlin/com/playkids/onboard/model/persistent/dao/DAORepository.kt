package com.playkids.onboard.model.persistent.dao

import com.playkids.onboard.model.persistent.entity.EventLog
import com.playkids.onboard.model.persistent.entity.Lottery
import com.playkids.onboard.model.persistent.entity.Ticket
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.model.persistent.table.EventLogTable
import com.playkids.onboard.model.persistent.table.LotteryTable
import com.playkids.onboard.model.persistent.table.TicketTable
import com.playkids.onboard.model.persistent.table.UserTable
import org.jetbrains.exposed.dao.IntEntityClass

/**
 * User DAO.
 */
abstract class UserDAO : IntEntityClass<User>(UserTable)

/**
 * Ticket DAO.
 */
abstract class TicketDAO : IntEntityClass<Ticket>(TicketTable)

/**
 * Lottery DAO.
 */
abstract class LotteryDAO : IntEntityClass<Lottery>(LotteryTable)

/**
 * EventLog DAO.
 */
abstract class EventLogDAO : IntEntityClass<EventLog>(EventLogTable)