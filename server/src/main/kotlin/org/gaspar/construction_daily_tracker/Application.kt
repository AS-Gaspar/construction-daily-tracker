package org.gaspar.construction_daily_tracker

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.gaspar.construction_daily_tracker.database.DatabaseFactory
import org.gaspar.construction_daily_tracker.repository.*
import org.gaspar.construction_daily_tracker.routes.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Inicializar banco de dados
    DatabaseFactory.init()

    // Configurar plugins
    install(ContentNegotiation) {
        json()
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = io.ktor.http.HttpStatusCode.InternalServerError)
        }
    }

    // Inicializar repositórios
    val workRepo = WorkRepository()
    val roleRepo = RoleRepository()
    val employeeRepo = EmployeeRepository()
    val adjustmentRepo = DayAdjustmentRepository()
    val payrollRepo = MonthlyPayrollRepository()

    // Configurar rotas
    routing {
        get("/") {
            call.respondText("Construction Daily Tracker API")
        }

        workRoutes(workRepo)
        roleRoutes(roleRepo)
        employeeRoutes(employeeRepo)
        dayAdjustmentRoutes(adjustmentRepo, payrollRepo, employeeRepo)
        monthlyPayrollRoutes(payrollRepo, employeeRepo)
    }
}