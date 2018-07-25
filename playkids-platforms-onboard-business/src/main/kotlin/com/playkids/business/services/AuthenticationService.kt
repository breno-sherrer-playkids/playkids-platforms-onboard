package com.playkids.business.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.playkids.business.auth.SecurityToken
import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.onboard.model.persistent.entity.User
import java.time.Clock
import java.time.Duration
import java.util.*

/**
 * Authentication Service.
 *
 * Handles authentication and Security Tokens.
 */
class AuthenticationService(
        private val userService: UserService) {

    private val jwtAlgorithm = Algorithm.HMAC512("don't-tell-people-this-secret")

    private val jwtVerifier = (JWT.require(jwtAlgorithm) as JWTVerifier.BaseVerification).build()

    /**
     * Generates an [SecurityToken] for a given JWT Token.
     */
    suspend fun generateUserSecurityToken(token: String): SecurityToken? =
            decodeToken(token)
                    ?.let { userService.findByEmail(ServerSecurityToken, it.getClaim(Claims.USERNAME).asString()) }
                    ?.let { UserSecurityToken(it) }

    /**
     * Decodes an JWT Token, verifying its integrity.
     */
    private fun decodeToken(token: String): DecodedJWT? =
            try {
                jwtVerifier.verify(token)
            } catch (exception: JWTVerificationException) {
                null
            }

    /**
     * Generates a JWT Token for a given User.
     */
    fun generateToken(user: User): String =
            JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withClaim(Claims.USERNAME, user.email)
                    .withExpiresAt(Date(Clock.systemDefaultZone().instant().plus(TOKEN_TTL).toEpochMilli()))
                    .sign(jwtAlgorithm)

    /**
     * Configuration Companion.
     */
    companion object {

        private val JWT_ISSUER = "playkids-onboard-platforms"
        private val TOKEN_TTL = Duration.ofDays(30)

        /**
         * Defines the properties that are going to be set on the JWT Token payload.
         */
        object Claims {
            val USERNAME = "username"
        }
    }
}