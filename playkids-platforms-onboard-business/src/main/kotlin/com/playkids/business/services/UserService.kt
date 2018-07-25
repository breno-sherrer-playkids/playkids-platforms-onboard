package com.playkids.business.services

import com.playkids.auth.SecurityToken
import com.playkids.auth.UserSecurityToken
import com.playkids.onboard.model.persistent.DatabaseConfigurator
import com.playkids.onboard.model.persistent.constants.UserConstants
import com.playkids.onboard.model.persistent.entity.User
import com.playkids.onboard.model.persistent.table.UserTable
import com.playkids.onboard.utils.DomainException
import com.playkids.onboard.utils.ValidationIssue
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
                    issues.add(UserValidationIssues.USERNAME_BLANK)

                } else if (userName!!.length < 5) {
                    issues.add(UserValidationIssues.USERNAME_TOO_SHORT)

                } else {

                    val userSameUsername = userDAO.find {
                        UserTable.userName eq userName
                    }

                    if (userSameUsername.count() != 0) {
                        issues.add(UserValidationIssues.USERNAME_DUPLICATED)
                    }
                }

                if (password.isNullOrBlank()) {
                    issues.add(UserValidationIssues.PASSWORD_BLANK)
                } else if (password!!.length < 8) {
                    issues.add(UserValidationIssues.PASSWORD_TOO_SHORT)
                }

                if (!issues.isEmpty()) {
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
            throw DomainException(listOf(UserValidationIssues.CREDITS_INVALID_QUANTITY))
        }

        if (securityToken !is UserSecurityToken) {
            throw IllegalStateException("Tried to buy credits without an User Token")
        }

        val user = findByUsername(securityToken, securityToken.principal)!!

        DatabaseConfigurator.transactionalContext {
            user.credits = user.credits.add(quantity.setScale(2, RoundingMode.HALF_UP))
        }
    }

    /**
     * Enum defining the Validation issues for an User Entity.
     */
    enum class UserValidationIssues(
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