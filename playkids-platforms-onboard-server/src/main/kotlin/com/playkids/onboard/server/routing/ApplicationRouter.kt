package com.playkids.onboard.server.routing

import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.UserService
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.server.routing.routes.AuthenticationRoute
import com.playkids.onboard.server.routing.routes.MainRoute
import com.playkids.onboard.server.routing.routes.UserRoute
import io.ktor.routing.Routing
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * Singleton used for route registration and initialization.
 */
object ApplicationRouter {

    private val userService = UserService(User.DAO)
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