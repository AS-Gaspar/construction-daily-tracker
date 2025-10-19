package org.gaspar.construction_daily_tracker.database

import org.jetbrains.exposed.sql.Table

object Works : Table("works") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}

object Roles : Table("roles") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 100)

    override val primaryKey = PrimaryKey(id)
}

object Employees : Table("employees") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val roleId = integer("role_id").references(Roles.id)
    val workId = integer("work_id").references(Works.id)
    val dailyValue = decimal("daily_value", 10, 2)

    override val primaryKey = PrimaryKey(id)
}

object DayAdjustments : Table("day_adjustments") {
    val id = integer("id").autoIncrement()
    val employeeId = integer("employee_id").references(Employees.id)
    val date = varchar("date", 10) // yyyy-MM-dd
    val adjustmentValue = decimal("adjustment_value", 5, 2) // pode ser negativo ou positivo
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}

object MonthlyPayrolls : Table("monthly_payrolls") {
    val id = integer("id").autoIncrement()
    val employeeId = integer("employee_id").references(Employees.id)
    val periodStartDate = varchar("period_start_date", 10) // yyyy-MM-dd
    val periodEndDate = varchar("period_end_date", 10)     // yyyy-MM-dd
    val baseWorkdays = decimal("base_workdays", 5, 2)
    val finalWorkedDays = decimal("final_worked_days", 5, 2)
    val totalPayment = decimal("total_payment", 10, 2)
    val closedAt = long("closed_at").nullable() // timestamp em milissegundos

    override val primaryKey = PrimaryKey(id)
}

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100).uniqueIndex()
    val password = varchar("password", 255)

    override val primaryKey = PrimaryKey(id)
}
