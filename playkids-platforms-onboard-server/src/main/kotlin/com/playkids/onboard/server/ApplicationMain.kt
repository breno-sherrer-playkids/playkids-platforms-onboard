package com.playkids.onboard.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.playkids.onboard.server.commons.SimpleJWT
import com.playkids.onboard.server.routing.ApplicationRouter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.IntEntity

val simpleJwt = SimpleJWT("my-super-secret-for-jwt")

/**
 * Main Application, define the routing mechanism and install features.
 *
 * NOTE: the main function is called by Ktor Engine (io.ktor.server.netty.DevelopmentEngine), configured inside
 * application.conf.
 */
fun Application.main() {

    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)

            // Override the annotation introspector to ignore all IntEntity/Entity classes, since they delegate the
            // field properties to the respective Table class
            setAnnotationIntrospector(object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember) =
                        (m.declaringClass == IntEntity::class.java)
                                || (m.declaringClass == Entity::class.java)
                                || super.hasIgnoreMarker(m)
            })
        }
    }


    install(Authentication) {
        jwt {

            verifier(simpleJwt.verifier)

            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    install(Routing) {
        ApplicationRouter.initRoutes(this)
    }
}