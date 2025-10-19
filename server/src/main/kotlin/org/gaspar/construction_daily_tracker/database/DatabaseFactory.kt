package org.gaspar.construction_daily_tracker.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/construction_tracker"
            driverClassName = "org.postgresql.Driver"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "postgres"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
    }
}
