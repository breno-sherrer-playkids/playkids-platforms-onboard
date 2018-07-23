package com.playkids.onboard.server.commons

import java.util.*

class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
        listOf(User("test", "test"))
                .associateBy { it.name }
                .toMutableMap()
)

class LoginRegister(val user: String, val password: String)