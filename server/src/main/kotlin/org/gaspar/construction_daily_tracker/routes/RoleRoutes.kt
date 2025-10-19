package org.gaspar.construction_daily_tracker.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.gaspar.construction_daily_tracker.repository.RoleRepository

@Serializable
data class CreateRoleRequest(val title: String)

@Serializable
data class UpdateRoleRequest(val title: String)

fun Route.roleRoutes(repository: RoleRepository) {
    route("/roles") {
        get {
            val roles = repository.findAll()
            call.respond(roles)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            val role = repository.findById(id)
            if (role == null) {
                call.respond(HttpStatusCode.NotFound, "Cargo não encontrado")
            } else {
                call.respond(role)
            }
        }

        post {
            val request = call.receive<CreateRoleRequest>()
            val role = repository.create(request.title)
            call.respond(HttpStatusCode.Created, role)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            val request = call.receive<UpdateRoleRequest>()
            val updated = repository.update(id, request.title)

            if (updated) {
                call.respond(HttpStatusCode.OK, "Cargo atualizado")
            } else {
                call.respond(HttpStatusCode.NotFound, "Cargo não encontrado")
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
                call.respond(HttpStatusCode.OK, "Cargo deletado")
            } else {
                call.respond(HttpStatusCode.NotFound, "Cargo não encontrado")
            }
        }
    }
}
