package com.playkids.onboard.server.routing

import com.playkids.onboard.server.routing.routes.LoginRoute
import com.playkids.onboard.server.routing.routes.MainRoute
import com.playkids.onboard.server.routing.routes.UserRoute
import io.ktor.routing.Routing
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * Singleton used for route registration and initialization.
 */
object ApplicationRouter {

    /**
     * Routes that are going to be registered.
     */
    private val routeServices = setOf(
            MainRoute,
            LoginRoute,
            UserRoute
    )

    /**
     * Initializes the Routes.
     */
    fun initRoutes(routing: Routing) {
        routeServices.forEach {
            InternalLoggerFactory.getInstance("ApplicationRouter")
                    .info("Registering Route for '${it.javaClass.simpleName}'")

            it.startRoute(routing)
        }
    }
}