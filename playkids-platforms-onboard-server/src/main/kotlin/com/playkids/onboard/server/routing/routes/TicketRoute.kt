package com.playkids.onboard.server.routing.routes

import com.playkids.business.auth.authenticate
import com.playkids.business.auth.securityToken
import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.TicketService
import com.playkids.onboard.model.persistent.entity.dto
import com.playkids.onboard.server.routing.Routable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

/**
 * Defines the Ticket route.
 */
class TicketRoute(
        private val authenticationService: AuthenticationService,
        private val ticketService: TicketService) : Routable() {

    override val servicePath: String
        get() = "/ticket"

    override fun configureRoute(route: Route) {
        route {

            authenticate(authenticationService)

            /**
             * Gets all Tickets owned by User.
             */
            get {
                call.respond(ticketService.findAllOwnedByUser(call.securityToken()).map { it.dto() })
            }

            /**
             * Buys a Ticket.
             */
            post {
                val (idLottery) = call.receive(BuyTicketRequest::class)

                ticketService.buyTicket(call.securityToken(), idLottery)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

/**
 * Request payload for a Ticket purchase.
 */
private data class BuyTicketRequest(val idLottery: Int? = null)