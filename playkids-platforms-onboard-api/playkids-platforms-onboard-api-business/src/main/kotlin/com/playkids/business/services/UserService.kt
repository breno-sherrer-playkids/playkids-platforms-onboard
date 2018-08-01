package com.playkids.business.services

import com.playkids.business.auth.SecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.business.event.Event
import com.playkids.business.event.EventLogger
import com.playkids.onboard.commons.ValidationException
import com.playkids.onboard.commons.ValidationIssue
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.constants.UserConstants
import com.playkids.onboard.model.persistent.dao.UserDAO
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.model.persistent.table.UserTable
import kotlinx.coroutines.experimental.launch
import org.mindrot.jbcrypt.BCrypt
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Provides functionalities related to the "User" Entity.
 */
class UserService(
        private val eventLogger: EventLogger,
        private val userDAO: UserDAO) {

    /**
     * Creates an User Entity.
     */
    suspend fun createUser(securityToken: SecurityToken, email: String?, password: String?): User =
            DatabaseConfigurator.transactionalContext {

                val issues = mutableListOf<ValidationIssue>()

                if (email.isNullOrBlank()) {
                    issues.add(UserValidationIssue.EMAIL_BLANK)

                } else {
                    val validEmailRegex =
                            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

                    if (!email!!.matches(Regex(validEmailRegex))) {
                        issues.add(UserValidationIssue.EMAIL_INVALID)

                    } else {

                        val userSameEmail = userDAO.find {
                            UserTable.email eq email
                        }

                        if (userSameEmail.count() != 0) {
                            issues.add(UserValidationIssue.EMAIL_DUPLICATED)
                        }
                    }
                }

                if (password.isNullOrBlank()) {
                    issues.add(UserValidationIssue.PASSWORD_BLANK)
                } else if (password!!.length < 8) {
                    issues.add(UserValidationIssue.PASSWORD_TOO_SHORT)
                }

                if (issues.isNotEmpty()) {
                    throw ValidationException(issues)
                }

                launch {
                    eventLogger.log(securityToken, Event.USER_CREATION)
                }

                return@transactionalContext userDAO.new {
                    this.email = email!!
                    this.password = BCrypt.hashpw(password!!, BCrypt.gensalt())
                    this.credits = BigDecimal(UserConstants.DEFAULT_CREDITS_SIGNUP)
                }
            }

    /**
     * Find an User by its E-Mail.
     */
    suspend fun findByEmail(securityToken: SecurityToken, email: String) =
            DatabaseConfigurator.transactionalContext {

                userDAO.find {
                    UserTable.email eq email
                }.firstOrNull()
            }

    /**
     * Find a matching User by its email and password.
     */
    suspend fun findByCredentials(securityToken: SecurityToken, email: String, password: String) =
            DatabaseConfigurator.transactionalContext {

                userDAO.find {
                    UserTable.email eq email
                }.firstOrNull {
                    BCrypt.checkpw(password, it.password)
                }
            }

    /**
     * Buy Credits to an User identified by the Security Token.
     */
    suspend fun buyCredits(securityToken: SecurityToken, quantity: BigDecimal?) {

        if (quantity == null || quantity <= BigDecimal.ZERO) {
            throw ValidationException(listOf(UserValidationIssue.CREDITS_INVALID_QUANTITY))
        }

        if (securityToken !is UserSecurityToken) {
            throw IllegalStateException("Tried to buy credits without an User Token")
        }

        DatabaseConfigurator.transactionalContext {
            val user = securityToken.user

            user.credits = user.credits.add(quantity.setScale(2, RoundingMode.HALF_UP))
        }

        eventLogger.log(securityToken, Event.USER_CREDIT_PURCHASE)
    }

    /**
     * Gets and consumes an User Congratulation flag.
     */
    suspend fun getAndConsumeCongratulations(securityToken: SecurityToken): Boolean {

        if (securityToken !is UserSecurityToken) {
            throw IllegalStateException("Tried to consume congratulations without an User Token")
        }

        if (securityToken.user.congratulate) {

            DatabaseConfigurator.transactionalContext {
                securityToken.user.congratulate = false
            }

            eventLogger.log(securityToken, Event.USER_CONGRATULATION)

            return true
        }

        return false
    }

    /**
     * Enum defining the Validation issues for an User Entity.
     */
    enum class UserValidationIssue(
            override val title: String,
            override val description: String) : ValidationIssue {

        EMAIL_BLANK(
                "Failed to create an User",
                "E-Mail must be informed"),

        EMAIL_INVALID(
                "Failed to create an User",
                "E-Mail invalid"),

        EMAIL_DUPLICATED(
                "Failed to create an User",
                "E-Mail already exists"),

        PASSWORD_BLANK(
                "Failed to create an User",
                "Password must be informed"),

        PASSWORD_TOO_SHORT(
                "Failed to create an User",
                "Password must have at least 8 characters"),

        CREDITS_INVALID_QUANTITY(
                "Failed to buy Credits",
                "The quantity must be greater than Zero"
        )
    }
}