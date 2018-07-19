package com.playkids.onboard.routing

import io.ktor.routing.Routing

interface Routable {

    fun configureRoute(routing: Routing)
}