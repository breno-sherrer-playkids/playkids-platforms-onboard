package com.playkids.business.services

import com.nhaarman.mockito_kotlin.mock
import com.playkids.business.event.EventLogger
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * Tests for the [EmailService] module
 */
class EmailServiceTest : Spek({
    val mockEventLogger = mock<EventLogger>()

    val emailService = EmailService(mockEventLogger)

    describe("an EmailService") {

        on("sending an E-Mail") {

            it("should send an email") {
                runBlocking {
                    emailService.sendEmail("breno.sherrer@playkids.com", "unit tests", "working")
                }
            }
        }
    }
})