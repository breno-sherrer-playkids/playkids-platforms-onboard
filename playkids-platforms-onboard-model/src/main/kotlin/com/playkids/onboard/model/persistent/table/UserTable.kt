package com.playkids.onboard.model.persistent.table

import org.jetbrains.exposed.dao.IntIdTable

/**
 * "User" Table Definition.
 */
object UserTable : IntIdTable("user") {

    val email =
            varchar("email", 80)
                    .uniqueIndex()

    val password =
            text("password")

    val credits =
            decimal("credits", 10, 4)

    val congratulate =
            bool("congratulate")
                    .default(false)
}