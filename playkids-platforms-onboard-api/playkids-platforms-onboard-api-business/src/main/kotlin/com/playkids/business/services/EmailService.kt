package com.playkids.business.services

import com.playkids.business.auth.ServerSecurityToken
import com.playkids.business.event.Event
import com.playkids.business.event.EventLogger
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail

/**
 * Environment variable that holds the e-mail.
 */
const val ENV_EMAIL_USER = "email_user"

/**
 * Environment variable that holds teh e-mail password.
 */
const val ENV_EMAIL_PASSWORD = "email_password"

/**
 * E-Mail Services.
 */
class EmailService(
        private val eventLogger: EventLogger) {

    /**
     * Sends an E-Mail with the given information.
     */
    suspend fun sendEmail(destination: String, subject: String, message: String) {

        val sender = System.getenv(ENV_EMAIL_USER)
        val password = System.getenv(ENV_EMAIL_PASSWORD)

        val email = HtmlEmail()
        email.hostName = "smtp.googlemail.com"
        email.setSmtpPort(587)
        email.setAuthenticator(DefaultAuthenticator(sender, password))
        email.isSSLOnConnect = true
        email.setFrom(sender)
        email.addTo(destination)

        email.subject = subject
        email.setHtmlMsg(message)

        email.send()

        eventLogger.log(ServerSecurityToken, Event.EMAIL_SENDING)
    }
}