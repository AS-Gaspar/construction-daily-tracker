package org.gaspar.construction_daily_tracker.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.gaspar.construction_daily_tracker.repository.MonthlyPayrollRepository
import org.gaspar.construction_daily_tracker.repository.EmployeeRepository
import org.gaspar.construction_daily_tracker.repository.DayAdjustmentRepository
import org.gaspar.construction_daily_tracker.utils.calculateWorkDays
import org.gaspar.construction_daily_tracker.utils.getCurrentMonthStart
import org.gaspar.construction_daily_tracker.utils.getCurrentMonthEnd
import java.math.BigDecimal

@Serializable
data class CreateMonthlyPayrollRequest(
    val employeeId: Int,
    val periodStartDate: String,
    val periodEndDate: String
)

@Serializable
data class GeneratePayrollsRequest(
    val periodStartDate: String? = null,
    val periodEndDate: String? = null
)

@Serializable
data class GeneratePayrollsResponse(
    val generated: Int,
    val skipped: Int,
    val periodStartDate: String,
    val periodEndDate: String,
    val baseWorkdays: Int
)

fun Route.monthlyPayrollRoutes(
    payrollRepo: MonthlyPayrollRepository,
    employeeRepo: EmployeeRepository,
    adjustmentRepo: DayAdjustmentRepository
) {
    route("/monthly-payrolls") {
        // Get all payrolls
        get {
            val payrolls = payrollRepo.findAll()
            call.respond(payrolls)
        }

        // Generate payrolls for all employees for a period
        post("/generate") {
            val request = call.receive<GeneratePayrollsRequest>()

            // Use current period if not specified
            val periodStart = request.periodStartDate ?: getCurrentMonthStart()
            val periodEnd = request.periodEndDate ?: getCurrentMonthEnd()

            // Calculate base workdays for the period (Monday-Friday only)
            val baseWorkdays = calculateWorkDays(periodStart, periodEnd)
            val baseWorkdaysBD = BigDecimal(baseWorkdays)

            // Get all employees
            val employees = employeeRepo.findAll()
            var generated = 0
            var skipped = 0

            employees.forEach { employee ->
                // Check if employee already has an active payroll for this period
                val existingPayroll = payrollRepo.findActiveByEmployeeId(employee.id)
                if (existingPayroll != null &&
                    existingPayroll.periodStartDate == periodStart &&
                    existingPayroll.periodEndDate == periodEnd) {
                    skipped++
                    return@forEach
                }

                // Get all adjustments for this employee in this period
                val adjustments = adjustmentRepo.findByEmployeeId(employee.id)
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
                payrollRepo.create(
                    employee.id,
                    periodStart,
                    periodEnd,
                    baseWorkdaysBD,
                    finalWorkedDays,
                    totalPayment
                )

                generated++
            }

            val response = GeneratePayrollsResponse(
                generated = generated,
                skipped = skipped,
                periodStartDate = periodStart,
                periodEndDate = periodEnd,
                baseWorkdays = baseWorkdays
            )

            call.respond(HttpStatusCode.Created, response)
        }

        post {
            val request = call.receive<CreateMonthlyPayrollRequest>()

            // Buscar funcionário para obter daily_value
            val employee = employeeRepo.findById(request.employeeId)
            if (employee == null) {
                call.respond(HttpStatusCode.NotFound, "Funcionário não encontrado")
                return@post
            }

            // Calcular dias úteis base do período
            val baseWorkdays = calculateWorkDays(request.periodStartDate, request.periodEndDate)
            val baseWorkdaysBD = BigDecimal(baseWorkdays)

            // Inicialmente, finalWorkedDays = baseWorkdays
            val dailyValue = BigDecimal(employee.dailyValue)
            val totalPayment = baseWorkdaysBD.multiply(dailyValue)

            val payroll = payrollRepo.create(
                request.employeeId,
                request.periodStartDate,
                request.periodEndDate,
                baseWorkdaysBD,
                baseWorkdaysBD,
                totalPayment
            )

            call.respond(HttpStatusCode.Created, payroll)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            val payroll = payrollRepo.findById(id)
            if (payroll == null) {
                call.respond(HttpStatusCode.NotFound, "Folha de pagamento não encontrada")
            } else {
                call.respond(payroll)
            }
        }

        get("/employee/{employeeId}") {
            val employeeId = call.parameters["employeeId"]?.toIntOrNull()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do funcionário inválido")
                return@get
            }

            val payrolls = payrollRepo.findByEmployeeId(employeeId)
            call.respond(payrolls)
        }

        get("/employee/{employeeId}/active") {
            val employeeId = call.parameters["employeeId"]?.toIntOrNull()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do funcionário inválido")
                return@get
            }

            val payroll = payrollRepo.findActiveByEmployeeId(employeeId)
            if (payroll == null) {
                call.respond(HttpStatusCode.NotFound, "Nenhuma folha ativa encontrada")
            } else {
                call.respond(payroll)
            }
        }

        get("/active") {
            val activePayrolls = payrollRepo.findAllActive()
            call.respond(activePayrolls)
        }

        put("/{id}/close") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            val closed = payrollRepo.close(id)
            if (closed) {
                call.respond(HttpStatusCode.OK, "Folha de pagamento fechada")
            } else {
                call.respond(HttpStatusCode.NotFound, "Folha de pagamento não encontrada")
            }
        }
    }
}
