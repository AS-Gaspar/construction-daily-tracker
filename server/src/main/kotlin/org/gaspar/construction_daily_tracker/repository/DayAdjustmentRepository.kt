package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.database.DayAdjustments
import org.gaspar.construction_daily_tracker.model.DayAdjustment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class DayAdjustmentRepository {

    fun create(
        employeeId: Int,
        date: String,
        adjustmentValue: BigDecimal,
        notes: String? = null
    ): DayAdjustment = transaction {
        val id = DayAdjustments.insert {
            it[DayAdjustments.employeeId] = employeeId
            it[DayAdjustments.date] = date
            it[DayAdjustments.adjustmentValue] = adjustmentValue
            it[DayAdjustments.notes] = notes
        }[DayAdjustments.id]

        DayAdjustment(id, employeeId, date, adjustmentValue.toString(), notes)
    }

    fun findById(id: Int): DayAdjustment? = transaction {
        DayAdjustments.selectAll().where { DayAdjustments.id eq id }
            .map { toDayAdjustment(it) }
            .singleOrNull()
    }

    fun findByEmployeeId(employeeId: Int): List<DayAdjustment> = transaction {
        DayAdjustments.selectAll().where { DayAdjustments.employeeId eq employeeId }
            .orderBy(DayAdjustments.date to SortOrder.DESC)
            .map { toDayAdjustment(it) }
    }

    fun findByEmployeeIdAndDateRange(employeeId: Int, startDate: String, endDate: String): List<DayAdjustment> = transaction {
        DayAdjustments.selectAll()
            .where {
                (DayAdjustments.employeeId eq employeeId) and
                (DayAdjustments.date greaterEq startDate) and
                (DayAdjustments.date lessEq endDate)
            }
            .orderBy(DayAdjustments.date to SortOrder.ASC)
            .map { toDayAdjustment(it) }
    }

    fun delete(id: Int): Boolean = transaction {
        DayAdjustments.deleteWhere { DayAdjustments.id eq id } > 0
    }

    private fun toDayAdjustment(row: ResultRow) = DayAdjustment(
        id = row[DayAdjustments.id],
        employeeId = row[DayAdjustments.employeeId],
        date = row[DayAdjustments.date],
        adjustmentValue = row[DayAdjustments.adjustmentValue].toString(),
        notes = row[DayAdjustments.notes]
    )
}
