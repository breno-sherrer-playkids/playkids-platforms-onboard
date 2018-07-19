package com.playkids.onboard.routing.routes

import com.playkids.onboard.routing.Routable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

object MainRoute : Routable {

    override fun configureRoute(routing: Routing) {
        
        routing.get("/") {
            call.respond(HttpStatusCode.OK)
        }
    }
}