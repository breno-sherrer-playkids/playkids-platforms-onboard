package com.playkids.onboard.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.playkids.onboard.commons.DomainException
import com.playkids.onboard.server.routing.ApplicationRouter
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.IntEntity
import java.util.*

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

            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))

            registerModule(JodaModule())

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

    install(StatusPages) {

        // Custom exception handling
        exception<DomainException> {
            call.respond(it.httpStatusCode, it.errors)
        }
    }

    install(CORS)
    {
        anyHost()

        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        header(HttpHeaders.XForwardedProto)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.Origin)
        allowCredentials = true
//        maxAge = Duration.ofDays(1)
    }

    install(Routing) {
        ApplicationRouter.initRoutes(this)
    }
}