package org.gaspar.construction_daily_tracker.repository

import kotlinx.coroutines.flow.first
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.database.entities.DayAdjustmentEntity
import org.gaspar.construction_daily_tracker.model.DayAdjustment

class LocalDayAdjustmentRepository(private val database: AppDatabase) {
    private val dayAdjustmentDao = database.dayAdjustmentDao()
    private val payrollDao = database.monthlyPayrollDao()

    suspend fun getAllAdjustments(): List<DayAdjustment> {
        return dayAdjustmentDao.getAll().first().map { it.toModel() }
    }

    suspend fun getAdjustmentById(id: Int): DayAdjustment? {
        return dayAdjustmentDao.getById(id)?.toModel()
    }

    suspend fun createAdjustment(
        employeeId: Int,
        date: String,
        adjustmentValue: String,
        notes: String?
    ): DayAdjustment {
        val entity = DayAdjustmentEntity(
            employeeId = employeeId,
            date = date,
            adjustmentValue = adjustmentValue,
            notes = notes
        )
        val id = dayAdjustmentDao.insert(entity)

        // Update affected payrolls
        updateAffectedPayrolls(employeeId, date)

        return DayAdjustment(id.toInt(), employeeId, date, adjustmentValue, notes)
    }

    suspend fun deleteAdjustment(id: Int) {
        val adjustment = dayAdjustmentDao.getById(id)
        if (adjustment != null) {
            dayAdjustmentDao.deleteById(id)
            // Update affected payrolls
            updateAffectedPayrolls(adjustment.employeeId, adjustment.date)
        }
    }

    private suspend fun updateAffectedPayrolls(employeeId: Int, adjustmentDate: String) {
        // Find all active payrolls for this employee that cover this date
        val allPayrolls = payrollDao.getByEmployeeId(employeeId).first()
        val affectedPayrolls = allPayrolls.filter { payroll ->
            payroll.closedAt == null &&
            adjustmentDate >= payroll.periodStartDate &&
            adjustmentDate <= payroll.periodEndDate
        }

        // Recalculate each affected payroll
        affectedPayrolls.forEach { payroll ->
            val adjustments = dayAdjustmentDao.getByEmployeeId(employeeId).first()
            val periodAdjustments = adjustments.filter { adjustment ->
                adjustment.date >= payroll.periodStartDate &&
                adjustment.date <= payroll.periodEndDate
            }

            val totalAdjustment = periodAdjustments.sumOf { it.adjustmentValue.toBigDecimal() }
            val baseWorkdays = payroll.baseWorkdays.toBigDecimal()
            val finalWorkedDays = baseWorkdays.add(totalAdjustment)

            val employee = database.employeeDao().getById(employeeId)
            if (employee != null) {
                val dailyValue = employee.dailyValue.toBigDecimal()
                val totalPayment = finalWorkedDays.multiply(dailyValue)

                val updatedPayroll = payroll.copy(
                    finalWorkedDays = finalWorkedDays.toString(),
                    totalPayment = totalPayment.toString()
                )
                payrollDao.update(updatedPayroll)
            }
        }
    }

    private fun DayAdjustmentEntity.toModel() = DayAdjustment(id, employeeId, date, adjustmentValue, notes)
}
