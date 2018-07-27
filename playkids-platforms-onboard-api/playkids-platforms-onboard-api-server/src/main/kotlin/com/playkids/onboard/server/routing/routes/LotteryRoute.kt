package com.playkids.onboard.server.routing.routes

import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.auth.authenticate
import com.playkids.business.auth.securityToken
import com.playkids.business.services.AuthenticationService
import com.playkids.business.services.LotteryService
import com.playkids.onboard.model.persistent.entity.dto
import com.playkids.onboard.server.routing.Routable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime
import java.math.BigDecimal
import kotlin.concurrent.fixedRateTimer

/**
 * Defines the Lottery route.
 */
class LotteryRoute(
        private val authenticationService: AuthenticationService,
        private val lotteryService: LotteryService) : Routable() {

    override val servicePath: String
        get() = "/lottery"

    override fun configureRoute(route: Route) {
        route {

            authenticate(authenticationService)

            /**
             * Gets all Pending Lotteries.
             */
            get {
                call.respond(lotteryService.getAllPending(call.securityToken()).map { it.dto() })
            }

            /**
             * Creates a Lottery.
             */
            post {

                val (title, prize, ticketPrice, lotteryDateTime) = call.receive(CreateLotteryRequest::class)

                lotteryService.createLottery(call.securityToken(), title, prize, ticketPrice, lotteryDateTime)

                call.respond(HttpStatusCode.OK)
            }
        }

        launchLotteryRaffleTask()
    }

    /**
     * Launches a Fixed Timed-Task to Raffle the pending Lotteries.
     */
    private fun launchLotteryRaffleTask() =
            fixedRateTimer(null, true, 15_000, 600_000) {
                launch {
                    lotteryService.computePendingLotteryWinners(ServerSecurityToken)
                }
            }
}

/**
 * Request payload for a Lottery creation.
 */
private data class CreateLotteryRequest(
        val title: String? = null,
        val prize: BigDecimal? = null,
        val ticketPrice: BigDecimal? = null,
        val lotteryDateTime: DateTime? = null)