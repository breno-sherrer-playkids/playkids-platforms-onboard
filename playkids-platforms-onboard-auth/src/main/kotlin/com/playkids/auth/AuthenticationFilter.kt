package com.playkids.auth

import com.playkids.business.services.AuthenticationService
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.AttributeKey

val SECURITY_TOKEN_ATTRIBUTE = AttributeKey<SecurityToken>("security_token")

/**
 * Extensional function to deal with authentication, assuring that the API Consumer has a valid JWT Token in the
 * Authorization Header.
 */
fun Route.authenticate(authenticationService: AuthenticationService) {

    intercept(ApplicationCallPipeline.Infrastructure) {
        call.request.header(HttpHeaders.Authorization)
                ?.let { extractSecurityToken(authenticationService, it) }
                ?.also { call.attributes.put(SECURITY_TOKEN_ATTRIBUTE, it) }
                ?: call.respond(HttpStatusCode.Forbidden, "Invalid credentials")
    }
}

/**
 * Retrieves the Security Token for the given call.
 *
 * Note: this method can only be called inside a route that handles authentication, e.g.: don't use in the
 * authentication route ("/auth").
 */
fun ApplicationCall.securityToken(): SecurityToken =
        attributes[SECURITY_TOKEN_ATTRIBUTE]

/**
 * Extracts (creates) the Security Token from the given Authorization Header.
 *
 * The Header should be in the format: "Authorization: Bearer JWT-TOKEN"
 */
private suspend fun extractSecurityToken(authenticationService: AuthenticationService,
                                         authorizationHeader: String): SecurityToken? =
        authorizationHeader
                .takeIf { it.startsWith("Bearer ") }
                ?.substring(7)
                ?.let { authenticationService.generateUserSecurityToken(it) }