package com.playkids.business.auth

import com.playkids.onboard.model.persistent.entity.User

/**
 * Defines a Security Token that should be used to consume the API.
 */
sealed class SecurityToken(val principal: String)

object ServerSecurityToken : SecurityToken("server")
data class UserSecurityToken(val user: User) : SecurityToken(user.email)