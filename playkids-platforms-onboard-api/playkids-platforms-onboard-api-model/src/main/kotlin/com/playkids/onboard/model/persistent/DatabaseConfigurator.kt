package com.playkids.onboard.model.persistent

import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Configures the database and provide Transactional Coroutines.
 */
object DatabaseConfigurator {

    /**
     * DB lazy Instance.
     */
    private val db by lazy {
        Database.connect(
                url = "jdbc:postgresql://127.0.0.1/playkids-onboard-lottery",
                driver = "org.postgresql.Driver",
                user = "postgres",
                password = "root"
        )
    }

    /**
     * Coroutine Context.
     */
    private val coroutineContext: CoroutineContext = newFixedThreadPoolContext(4, "database-pool")

    /**
     * Provides a suspended block within a transaction.
     */
    suspend fun <T> transactionalContext(block: () -> T): T =
            withContext(coroutineContext) {

                // Initializes the database
                db

                transaction {
                    block()
                }
            }
}