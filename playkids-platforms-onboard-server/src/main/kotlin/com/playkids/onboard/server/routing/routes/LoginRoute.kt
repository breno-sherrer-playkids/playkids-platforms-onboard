package com.playkids.onboard.server.routing.routes

import com.playkids.onboard.server.commons.LoginRegister
import com.playkids.onboard.server.commons.User
import com.playkids.onboard.server.commons.users
import com.playkids.onboard.server.routing.Routable
import com.playkids.onboard.server.simpleJwt
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

/**
 *
 */
object LoginRoute : Routable() {

    override val SERVICE_PATH: String
        get() = "/auth"

    override fun configureRoute(route: Route) {
        route {

            post("/login-register") {

                val post = call.receive<LoginRegister>()
                val user = users.getOrPut(post.user) { User(post.user, post.password) }

                if (user.password != post.password) error("Invalid credentials")
                call.respond(mapOf("token" to simpleJwt.sign(user.name)))
            }
        }
    }
}