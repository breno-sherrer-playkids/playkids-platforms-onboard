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
object MainRoute : Routable() {

    override val SERVICE_PATH: String
        get() = ""

    override fun configureRoute(route: Route) {

        route.get("/") {
            call.respond(HttpStatusCode.OK)
        }
    }
}