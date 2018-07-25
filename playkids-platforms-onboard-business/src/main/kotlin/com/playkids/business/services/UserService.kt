package com.playkids.business.services

import com.playkids.business.auth.SecurityToken
import com.playkids.business.auth.UserSecurityToken
import com.playkids.onboard.commons.DomainException
import com.playkids.onboard.commons.EntityNotFoundException
import com.playkids.onboard.commons.ValidationIssue
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.constants.UserConstants
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.model.persistent.table.UserTable
import io.ktor.util.encodeBase64
import org.jetbrains.exposed.sql.and
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Provides functionalities related to the "User" Entity.
 */
class UserService(
        private val userDAO: User.DAO) {

    /**
     * Creates an User Entity.
     */
    suspend fun createUser(securityToken: SecurityToken, userName: String?, password: String?): User =
            DatabaseConfigurator.transactionalContext {

                val issues = mutableListOf<ValidationIssue>()

                if (userName.isNullOrBlank()) {
                    issues.add(UserValidationIssue.USERNAME_BLANK)

                } else if (userName!!.length < 5) {
                    issues.add(UserValidationIssue.USERNAME_TOO_SHORT)

                } else {

                    val userSameUsername = userDAO.find {
                        UserTable.userName eq userName
                    }

                    if (userSameUsername.count() != 0) {
                        issues.add(UserValidationIssue.USERNAME_DUPLICATED)
                    }
                }

                if (password.isNullOrBlank()) {
                    issues.add(UserValidationIssue.PASSWORD_BLANK)
                } else if (password!!.length < 8) {
                    issues.add(UserValidationIssue.PASSWORD_TOO_SHORT)
                }

                if (issues.isNotEmpty()) {
                    throw DomainException(issues)
                }

                return@transactionalContext userDAO.new {
                    this.userName = userName!!
                    this.password = encodeBase64(password!!.toByteArray())
                    this.credits = BigDecimal(UserConstants.DEFAULT_CREDITS_SIGNUP)
                }
            }

    /**
     * Find an User by its username.
     */
    suspend fun findByUsername(securityToken: SecurityToken, userName: String) =
            DatabaseConfigurator.transactionalContext {

                userDAO.find {
                    UserTable.userName eq userName
                }.firstOrNull()
            }

    /**
     * Find an matching User by its username and password.
     */
    suspend fun findByCredentials(securityToken: SecurityToken, userName: String, password: String) =
            DatabaseConfigurator.transactionalContext {

                userDAO.find {
                    UserTable.userName eq userName and (UserTable.password eq encodeBase64(password.toByteArray()))
                }.firstOrNull()
            }

    /**
     * Buy Credits to an User identified by the Security Token.
     */
    suspend fun buyCredits(securityToken: SecurityToken, quantity: BigDecimal?) {

        if (quantity == null || quantity <= BigDecimal.ZERO) {
            throw DomainException(listOf(UserValidationIssue.CREDITS_INVALID_QUANTITY))
        }

        if (securityToken !is UserSecurityToken) {
            throw IllegalStateException("Tried to buy credits without an User Token")
        }

        val user = userDAO.findById(securityToken.user.id)
                ?: throw EntityNotFoundException(User::class, securityToken.user.id.value)

        DatabaseConfigurator.transactionalContext {
            user.credits = user.credits.add(quantity.setScale(2, RoundingMode.HALF_UP))
        }
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

        USERNAME_BLANK(
                "Failed to create an User",
                "Username must be informed"),

        USERNAME_TOO_SHORT(
                "Failed to create an User",
                "Username must have at least 5 characters"),

        USERNAME_DUPLICATED(
                "Failed to create an User",
                "Username already exists"),

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