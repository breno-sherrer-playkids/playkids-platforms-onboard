package com.playkids.onboard.model.persistent.table

import org.jetbrains.exposed.dao.IntIdTable

/**
 * "User" Table Definition.
 */
object UserTable : IntIdTable("user") {

    val userName =
            varchar("username", 20)

    val password =
            text("password")

    val credits =
            decimal("credits", 10, 4)

    val congratulate =
            bool("congratulate")
                    .default(false)
}