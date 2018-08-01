package com.playkids.onboard.server.routing.routes

import com.playkids.onboard.server.routing.Routable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

/**
 * Default routing to access "/".
 */
class MainRoute : Routable() {

    override val servicePath: String
        get() = ""

    override fun configureRoute(route: Route) {

        route {

            /**
             * Heart-beat.
             */
            get("/health") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}