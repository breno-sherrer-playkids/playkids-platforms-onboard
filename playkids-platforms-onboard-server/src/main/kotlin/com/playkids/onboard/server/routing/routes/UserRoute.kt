package com.playkids.onboard.server.routing.routes

import com.playkids.onboard.server.routing.Routable
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

/**
 * Defines the "User" route.
 *
 * Path [/user]
 */
object UserRoute : Routable() {

    override val SERVICE_PATH: String
        get() = "/user"

    override fun configureRoute(route: Route) {
        route {

            get {
                call.respond("OK")
            }

            post {
                val receivedUser = call.receive<HashMap<String, Any>>()

                call.respond("Received: ${receivedUser.keys}")
            }
        }
    }
}
