package org.gaspar.construction_daily_tracker.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.gaspar.construction_daily_tracker.repository.DayAdjustmentRepository
import org.gaspar.construction_daily_tracker.repository.MonthlyPayrollRepository
import org.gaspar.construction_daily_tracker.repository.EmployeeRepository
import java.math.BigDecimal

@Serializable
data class CreateDayAdjustmentRequest(
    val employeeId: Int,
    val date: String,
    val adjustmentValue: String, // positivo para sábados, negativo para faltas
    val notes: String? = null
)

fun Route.dayAdjustmentRoutes(
    adjustmentRepo: DayAdjustmentRepository,
    payrollRepo: MonthlyPayrollRepository,
    employeeRepo: EmployeeRepository
) {
    route("/day-adjustments") {
        // Get all adjustments
        get {
            val adjustments = adjustmentRepo.findAll()
            call.respond(adjustments)
        }

        post {
            val request = call.receive<CreateDayAdjustmentRequest>()

            val adjustmentValue = try {
                BigDecimal(request.adjustmentValue)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Valor de ajuste inválido")
                return@post
            }

            // Validar valores permitidos
            val absValue = adjustmentValue.abs()
            if (absValue != BigDecimal("0.5") && absValue != BigDecimal("1.0")) {
                call.respond(HttpStatusCode.BadRequest, "Valor deve ser ±0.5 ou ±1.0")
                return@post
            }

            // Criar ajuste
            val adjustment = adjustmentRepo.create(
                request.employeeId,
                request.date,
                adjustmentValue,
                request.notes
            )

            // Atualizar folha de pagamento ativa
            val activePayroll = payrollRepo.findActiveByEmployeeId(request.employeeId)
            if (activePayroll != null) {
                val employee = employeeRepo.findById(request.employeeId)
                if (employee != null) {
                    val newFinalDays = BigDecimal(activePayroll.finalWorkedDays).add(adjustmentValue)
                    val newTotalPayment = newFinalDays.multiply(BigDecimal(employee.dailyValue))

                    payrollRepo.updateFinalWorkedDays(activePayroll.id, newFinalDays, newTotalPayment)
                }
            }

            call.respond(HttpStatusCode.Created, adjustment)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            val adjustment = adjustmentRepo.findById(id)
            if (adjustment == null) {
                call.respond(HttpStatusCode.NotFound, "Ajuste não encontrado")
            } else {
                call.respond(adjustment)
            }
        }

        get("/employee/{employeeId}") {
            val employeeId = call.parameters["employeeId"]?.toIntOrNull()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do funcionário inválido")
                return@get
            }

            val adjustments = adjustmentRepo.findByEmployeeId(employeeId)
            call.respond(adjustments)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }

            // Buscar ajuste antes de deletar para reverter na folha de pagamento
            val adjustment = adjustmentRepo.findById(id)
            if (adjustment == null) {
                call.respond(HttpStatusCode.NotFound, "Ajuste não encontrado")
                return@delete
            }

            val deleted = adjustmentRepo.delete(id)
            if (!deleted) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar")
                return@delete
            }

            // Reverter na folha de pagamento ativa
            val activePayroll = payrollRepo.findActiveByEmployeeId(adjustment.employeeId)
            if (activePayroll != null) {
                val employee = employeeRepo.findById(adjustment.employeeId)
                if (employee != null) {
                    val adjustmentValue = BigDecimal(adjustment.adjustmentValue)
                    val newFinalDays = BigDecimal(activePayroll.finalWorkedDays).subtract(adjustmentValue)
                    val newTotalPayment = newFinalDays.multiply(BigDecimal(employee.dailyValue))

                    payrollRepo.updateFinalWorkedDays(activePayroll.id, newFinalDays, newTotalPayment)
                }
            }

            call.respond(HttpStatusCode.OK, "Ajuste deletado")
        }
    }
}
