package org.gaspar.construction_daily_tracker.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.gaspar.construction_daily_tracker.repository.WorkRepository

@Serializable
data class CreateWorkRequest(val name: String)

@Serializable
data class UpdateWorkRequest(val name: String)

fun Route.workRoutes(repository: WorkRepository) {
    route("/works") {
        get {
            val works = repository.findAll()
            call.respond(works)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            val work = repository.findById(id)
            if (work == null) {
                call.respond(HttpStatusCode.NotFound, "Obra não encontrada")
            } else {
                call.respond(work)
            }
        }

        post {
            val request = call.receive<CreateWorkRequest>()
            val work = repository.create(request.name)
            call.respond(HttpStatusCode.Created, work)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            val request = call.receive<UpdateWorkRequest>()
            val updated = repository.update(id, request.name)

            if (updated) {
                call.respond(HttpStatusCode.OK, "Obra atualizada")
            } else {
                call.respond(HttpStatusCode.NotFound, "Obra não encontrada")
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
                call.respond(HttpStatusCode.OK, "Obra deletada")
            } else {
                call.respond(HttpStatusCode.NotFound, "Obra não encontrada")
            }
        }
    }
}
