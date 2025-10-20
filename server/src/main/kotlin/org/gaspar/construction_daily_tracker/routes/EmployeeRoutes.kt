package org.gaspar.construction_daily_tracker.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.gaspar.construction_daily_tracker.repository.EmployeeRepository
import java.math.BigDecimal

@Serializable
data class CreateEmployeeRequest(
    val name: String,
    val surname: String,
    val roleId: Int,
    val workId: Int,
    val dailyValue: String
)

@Serializable
data class UpdateEmployeeRequest(
    val name: String? = null,
    val surname: String? = null,
    val roleId: Int? = null,
    val workId: Int? = null,
    val dailyValue: String? = null
)

fun Route.employeeRoutes(repository: EmployeeRepository) {
    route("/employees") {
        get {
            val employees = repository.findAll()
            call.respond(employees)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            val employee = repository.findById(id)
            if (employee == null) {
                call.respond(HttpStatusCode.NotFound, "Funcionário não encontrado")
            } else {
                call.respond(employee)
            }
        }

        get("/work/{workId}") {
            val workId = call.parameters["workId"]?.toIntOrNull()
            if (workId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID da obra inválido")
                return@get
            }

            val employees = repository.findByWorkId(workId)
            call.respond(employees)
        }

        get("/role/{roleId}") {
            val roleId = call.parameters["roleId"]?.toIntOrNull()
            if (roleId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do cargo inválido")
                return@get
            }

            val employees = repository.findByRoleId(roleId)
            call.respond(employees)
        }

        post {
            val request = call.receive<CreateEmployeeRequest>()
            val dailyValue = try {
                BigDecimal(request.dailyValue)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Valor diário inválido")
                return@post
            }

            val employee = repository.create(
                request.name,
                request.surname,
                request.roleId,
                request.workId,
                dailyValue
            )
            call.respond(HttpStatusCode.Created, employee)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            val request = call.receive<UpdateEmployeeRequest>()
            val dailyValue = request.dailyValue?.let {
                try {
                    BigDecimal(it)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Valor diário inválido")
                    return@put
                }
            }

            val updated = repository.update(
                id,
                request.name,
                request.surname,
                request.roleId,
                request.workId,
                dailyValue
            )

            if (updated != null) {
                call.respond(HttpStatusCode.OK, updated)
            } else {
                call.respond(HttpStatusCode.NotFound, "Funcionário não encontrado")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }

            val deleted = repository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, "Funcionário deletado")
            } else {
                call.respond(HttpStatusCode.NotFound, "Funcionário não encontrado")
            }
        }
    }
}
