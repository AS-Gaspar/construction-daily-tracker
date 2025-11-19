package org.gaspar.construction_daily_tracker.repository

import kotlinx.coroutines.flow.first
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.database.entities.MonthlyPayrollEntity
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll
import org.gaspar.construction_daily_tracker.utils.calculateWorkDays
import org.gaspar.construction_daily_tracker.utils.getCurrentMonthEnd
import org.gaspar.construction_daily_tracker.utils.getCurrentMonthStart
import java.math.BigDecimal

class LocalPayrollRepository(private val database: AppDatabase) {
    private val payrollDao = database.monthlyPayrollDao()
    private val employeeDao = database.employeeDao()
    private val adjustmentDao = database.dayAdjustmentDao()

    suspend fun getAllPayrolls(): List<MonthlyPayroll> {
        return payrollDao.getAll().first().map { it.toModel() }
    }

    suspend fun getPayrollById(id: Int): MonthlyPayroll? {
        return payrollDao.getById(id)?.toModel()
    }

    suspend fun generatePayroll(periodStartDate: String): List<MonthlyPayroll> {
        val periodStart = periodStartDate
        val periodEnd = getCurrentMonthEnd()

        // Calculate base workdays for the period (Monday-Friday only)
        val baseWorkdays = calculateWorkDays(periodStart, periodEnd)
        val baseWorkdaysBD = BigDecimal(baseWorkdays)

        // Get all employees
        val employees = employeeDao.getAll().first()
        val generatedPayrolls = mutableListOf<MonthlyPayroll>()

        employees.forEach { employee ->
            // Check if employee already has an active payroll for this period
            val existingPayrolls = payrollDao.getByEmployeeId(employee.id).first()
            val existingPayroll = existingPayrolls.find { payroll ->
                payroll.closedAt == null &&
                payroll.periodStartDate == periodStart &&
                payroll.periodEndDate == periodEnd
            }

            if (existingPayroll != null) {
                return@forEach
            }

            // Get all adjustments for this employee in this period
            val adjustments = adjustmentDao.getByEmployeeId(employee.id).first()
            val periodAdjustments = adjustments.filter { adjustment ->
                adjustment.date >= periodStart && adjustment.date <= periodEnd
            }

            // Calculate total adjustment value
            val totalAdjustment = periodAdjustments.sumOf {
                BigDecimal(it.adjustmentValue)
            }

            // Calculate final worked days and payment
            val finalWorkedDays = baseWorkdaysBD.add(totalAdjustment)
            val dailyValue = BigDecimal(employee.dailyValue)
            val totalPayment = finalWorkedDays.multiply(dailyValue)

            // Create payroll
            val payrollEntity = MonthlyPayrollEntity(
                employeeId = employee.id,
                periodStartDate = periodStart,
                periodEndDate = periodEnd,
                baseWorkdays = baseWorkdaysBD.toString(),
                finalWorkedDays = finalWorkedDays.toString(),
                totalPayment = totalPayment.toString(),
                closedAt = null
            )

            val id = payrollDao.insert(payrollEntity)
            generatedPayrolls.add(payrollEntity.copy(id = id.toInt()).toModel())
        }

        return generatedPayrolls
    }

    suspend fun closePayroll(id: Int): MonthlyPayroll? {
        val payroll = payrollDao.getById(id) ?: return null
        val updatedPayroll = payroll.copy(closedAt = System.currentTimeMillis())
        payrollDao.update(updatedPayroll)
        return updatedPayroll.toModel()
    }

    private fun MonthlyPayrollEntity.toModel() = MonthlyPayroll(
        id,
        employeeId,
        periodStartDate,
        periodEndDate,
        baseWorkdays,
        finalWorkedDays,
        totalPayment,
        closedAt
    )
}
