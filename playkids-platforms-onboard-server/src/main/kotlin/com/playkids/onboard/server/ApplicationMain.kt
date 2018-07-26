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
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
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

    install(Routing) {
        ApplicationRouter.initRoutes(this)
    }
}