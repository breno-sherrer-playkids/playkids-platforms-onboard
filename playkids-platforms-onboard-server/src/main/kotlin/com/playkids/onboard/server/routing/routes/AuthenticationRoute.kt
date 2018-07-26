package com.playkids.onboard.server.routing.routes

import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.UserService
import com.playkids.onboard.server.routing.Routable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

/**
 * Defines the Route for Authentication (SignIn, SignUp).
 */
class AuthenticationRoute(
        private val authenticationService: AuthenticationService,
        private val userService: UserService) : Routable() {

    override val servicePath: String
        get() = "/auth"

    override fun configureRoute(route: Route) {
        route {

            /**
             * "/signin" feature.
             */
            post("/signin") {

                val (username, password) = call.receive(AuthenticationRequest::class)

                if (username != null && password != null) {

                    val user = userService.findByCredentials(ServerSecurityToken, username, password)

                    if (user != null) {
                        call.respond(AuthenticationResponse(authenticationService.generateToken(user)))
                    }
                }

                call.respond(HttpStatusCode.Forbidden, "Invalid credentials")
            }

            /**
             * "/signup" feature.
             */
            post("/signup") {
                val (username, password) = call.receive(AuthenticationRequest::class)

                val user = userService.createUser(ServerSecurityToken, username, password)

                call.respond(call.respond(AuthenticationResponse(authenticationService.generateToken(user))))
            }
        }
    }
}

private data class AuthenticationRequest(val username: String? = null, val password: String? = null)
private data class AuthenticationResponse(val token: String)