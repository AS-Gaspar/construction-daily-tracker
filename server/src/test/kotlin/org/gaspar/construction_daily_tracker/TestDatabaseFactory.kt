package org.gaspar.construction_daily_tracker

import org.gaspar.construction_daily_tracker.database.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseFactory {
    fun init() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(Works, Roles, Employees, DayAdjustments, MonthlyPayrolls)
        }
    }

    fun clean() {
        transaction {
            SchemaUtils.drop(Works, Roles, Employees, DayAdjustments, MonthlyPayrolls)
            SchemaUtils.create(Works, Roles, Employees, DayAdjustments, MonthlyPayrolls)
        }
    }
}
