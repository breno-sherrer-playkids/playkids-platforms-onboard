package com.playkids.onboard.server.routing

import com.playkids.business.event.DatabaseEventLogger
import com.playkids.business.services.*
import com.playkids.onboard.model.persistent.entity.EventLog
import com.playkids.onboard.model.persistent.entity.Lottery
import com.playkids.onboard.model.persistent.entity.Ticket
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.server.routing.routes.*
import io.ktor.routing.Routing
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * Singleton used for route registration and initialization.
 */
object ApplicationRouter {

    private val eventLogger = DatabaseEventLogger(EventLog.DAO)

    private val emailService = EmailService(eventLogger)
    private val userService = UserService(eventLogger, User.DAO)
    private val lotteryService = LotteryService(eventLogger, Lottery.DAO, emailService)
    private val ticketService = TicketService(eventLogger, Ticket.DAO, Lottery.DAO)

    private val authenticationService = AuthenticationService(userService)

    /**
     * Routes that are going to be registered.
     */
    private val routeServices = setOf(
            MainRoute(),

            UserRoute(
                    authenticationService,
                    userService
            ),

            AuthenticationRoute(
                    authenticationService,
                    userService
            ),

            LotteryRoute(
                    authenticationService,
                    lotteryService
            ),

            TicketRoute(
                    authenticationService,
                    ticketService
            )
    )

    /**
     * Initializes the Routes.
     */
    fun initRoutes(routing: Routing) {
        routeServices.forEach {
            InternalLoggerFactory
                    .getInstance(this::class.java)
                    .info("Registering Route for '${it.javaClass.simpleName}'")

            it.startRoute(routing)
        }
    }
}