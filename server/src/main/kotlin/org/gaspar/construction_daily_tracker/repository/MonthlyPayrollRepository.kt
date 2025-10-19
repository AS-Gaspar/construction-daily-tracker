package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.MonthlyPayrolls
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class MonthlyPayrollRepository {

    fun create(
        employeeId: Int,
        periodStartDate: String,
        periodEndDate: String,
        baseWorkdays: BigDecimal,
        finalWorkedDays: BigDecimal,
        totalPayment: BigDecimal
    ): MonthlyPayroll = transaction {
        val id = MonthlyPayrolls.insert {
            it[MonthlyPayrolls.employeeId] = employeeId
            it[MonthlyPayrolls.periodStartDate] = periodStartDate
            it[MonthlyPayrolls.periodEndDate] = periodEndDate
            it[MonthlyPayrolls.baseWorkdays] = baseWorkdays
            it[MonthlyPayrolls.finalWorkedDays] = finalWorkedDays
            it[MonthlyPayrolls.totalPayment] = totalPayment
            it[closedAt] = null
        }[MonthlyPayrolls.id]

        MonthlyPayroll(
            id,
            employeeId,
            periodStartDate,
            periodEndDate,
            baseWorkdays.toString(),
            finalWorkedDays.toString(),
            totalPayment.toString(),
            null
        )
    }

    fun findById(id: Int): MonthlyPayroll? = transaction {
        MonthlyPayrolls.selectAll().where { MonthlyPayrolls.id eq id }
            .map { toMonthlyPayroll(it) }
            .singleOrNull()
    }

    fun findByEmployeeId(employeeId: Int): List<MonthlyPayroll> = transaction {
        MonthlyPayrolls.selectAll().where { MonthlyPayrolls.employeeId eq employeeId }
            .orderBy(MonthlyPayrolls.periodStartDate to SortOrder.DESC)
            .map { toMonthlyPayroll(it) }
    }

    fun findActiveByEmployeeId(employeeId: Int): MonthlyPayroll? = transaction {
        MonthlyPayrolls.selectAll()
            .where { (MonthlyPayrolls.employeeId eq employeeId) and (MonthlyPayrolls.closedAt.isNull()) }
            .map { toMonthlyPayroll(it) }
            .singleOrNull()
    }

    fun findAllActive(): List<MonthlyPayroll> = transaction {
        MonthlyPayrolls.selectAll()
            .where { MonthlyPayrolls.closedAt.isNull() }
            .map { toMonthlyPayroll(it) }
    }

    fun updateFinalWorkedDays(id: Int, finalWorkedDays: BigDecimal, totalPayment: BigDecimal): Boolean = transaction {
        MonthlyPayrolls.update({ MonthlyPayrolls.id eq id }) {
            it[MonthlyPayrolls.finalWorkedDays] = finalWorkedDays
            it[MonthlyPayrolls.totalPayment] = totalPayment
        } > 0
    }

    fun close(id: Int): Boolean = transaction {
        MonthlyPayrolls.update({ MonthlyPayrolls.id eq id }) {
            it[closedAt] = System.currentTimeMillis()
        } > 0
    }

    private fun toMonthlyPayroll(row: ResultRow) = MonthlyPayroll(
        id = row[MonthlyPayrolls.id],
        employeeId = row[MonthlyPayrolls.employeeId],
        periodStartDate = row[MonthlyPayrolls.periodStartDate],
        periodEndDate = row[MonthlyPayrolls.periodEndDate],
        baseWorkdays = row[MonthlyPayrolls.baseWorkdays].toString(),
        finalWorkedDays = row[MonthlyPayrolls.finalWorkedDays].toString(),
        totalPayment = row[MonthlyPayrolls.totalPayment].toString(),
        closedAt = row[MonthlyPayrolls.closedAt]
    )
}
