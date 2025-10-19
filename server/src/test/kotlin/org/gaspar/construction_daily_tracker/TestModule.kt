package org.gaspar.construction_daily_tracker

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.gaspar.construction_daily_tracker.auth.ApiKeyAuth
import org.gaspar.construction_daily_tracker.repository.*
import org.gaspar.construction_daily_tracker.routes.*

fun Application.testModule() {
    // NOTE: Database is initialized in @BeforeClass with TestDatabaseFactory

    // Configure plugins
    install(ContentNegotiation) {
        json()
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowHeader("X-API-Key")
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = io.ktor.http.HttpStatusCode.InternalServerError)
        }
    }

    install(ApiKeyAuth) {
        apiKey = "test-api-key"
    }

    // Initialize repositories
    val workRepo = WorkRepository()
    val roleRepo = RoleRepository()
    val employeeRepo = EmployeeRepository()
    val adjustmentRepo = DayAdjustmentRepository()
    val payrollRepo = MonthlyPayrollRepository()

    // Configure routes
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
