package com.playkids.onboard.server.routing.routes

import com.playkids.business.auth.authenticate
import com.playkids.business.auth.securityToken
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
 * Defines the "User" route.
 */
class UserRoute(
        private val authenticationService: AuthenticationService,
        private val userService: UserService) : Routable() {

    override val servicePath: String
        get() = "/user"

    override fun configureRoute(route: Route) {
        route {

            authenticate(authenticationService)

            /**
             * Gets and Consumes a Congratulation flag.
             */
            post("/congratulate") {
                val response = mapOf("congratulate" to userService.getAndConsumeCongratulations(call.securityToken()))

                call.respond(response)
            }

            /**
             * Buy Credits.
             */
            post("/buy-credits") {

                val payload = call.receive<HashMap<String, Any>>()
                val quantity = (payload["quantity"] as Number?)?.toDouble()?.toBigDecimal()

                userService.buyCredits(call.securityToken(), quantity)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
