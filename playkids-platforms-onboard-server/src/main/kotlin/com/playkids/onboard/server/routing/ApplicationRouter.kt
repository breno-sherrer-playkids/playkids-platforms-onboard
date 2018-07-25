package com.playkids.onboard.server.routing

import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.LotteryService
import com.playkids.business.services.TicketService
import com.playkids.business.services.UserService
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

    private val userService = UserService(User.DAO)
    private val lotteryService = LotteryService(Lottery.DAO)
    private val ticketService = TicketService(Ticket.DAO, Lottery.DAO)

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