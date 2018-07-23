package com.playkids.onboard.server.routing

import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.route

/**
 * Default API path.
 */
private const val API_PATH = "/api"

/**
 * Defines the contract to classes who desires to define routes.
 */
abstract class Routable {

    /**
     * Initialization Flag.
     */
    var started: Boolean = false

    /**
     * Service specific path value, e.g.: "/user", "/car", "/billing".
     */
    abstract val SERVICE_PATH: String

    /**
     * Configures the desired route for the given [Route] object.
     */
    protected abstract fun configureRoute(route: Route)

    /**
     * Starts the configured Route.
     */
    fun startRoute(routing: Routing) {

        if (started)
            throw IllegalStateException("Route already started!")

        started = true

        routing {
            route(API_PATH + SERVICE_PATH) {
                configureRoute(this)
            }
        }
    }
}