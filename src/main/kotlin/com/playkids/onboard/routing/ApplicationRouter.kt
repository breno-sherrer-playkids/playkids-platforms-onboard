package com.playkids.onboard.routing

import com.playkids.onboard.routing.routes.MainRoute
import io.ktor.routing.Routing
import io.netty.util.internal.logging.InternalLoggerFactory

object ApplicationRouter {

    private val routeServices = setOf<Routable>(
            MainRoute
    )

    fun initRoutes(routing: Routing) {
        // TODO("Tentar melhorar utilizando reflection ou algo do tipopara descobrir automaticamente as instâncias de Routable")

        routeServices
                .forEach {
                    InternalLoggerFactory.getInstance("ApplicationRouter").info("Registering Route for '${it.javaClass.simpleName}'")
                    it.configureRoute(routing)
                }
    }
}