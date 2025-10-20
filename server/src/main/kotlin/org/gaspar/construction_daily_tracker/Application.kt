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
import org.gaspar.construction_daily_tracker.auth.ApiKeyAuth
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
        allowHeader("X-API-Key")
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = io.ktor.http.HttpStatusCode.InternalServerError)
        }
    }

    install(ApiKeyAuth)

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

        // Test endpoint with HTML for browser testing
        get("/test") {
            val serverUrl = "${call.request.local.scheme}://${call.request.local.localHost}:${call.request.local.localPort}"
            call.respondText("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>API Connection Test</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <style>
                        body { font-family: system-ui; padding: 20px; background: #f5f5f5; max-width: 600px; margin: 0 auto; }
                        .card { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                        h1 { color: #2563EB; margin: 0 0 10px 0; }
                        h2 { color: #333; margin: 15px 0 10px 0; font-size: 18px; }
                        .success { background: #d1fae5; padding: 15px; border-radius: 4px; color: #065f46; margin: 15px 0; }
                        .error { background: #fee2e2; padding: 15px; border-radius: 4px; color: #991b1b; margin: 15px 0; }
                        .info { background: #dbeafe; padding: 15px; border-radius: 4px; color: #1e40af; margin: 15px 0; }
                        code { background: #f3f4f6; padding: 2px 6px; border-radius: 3px; font-size: 13px; word-break: break-all; }
                        button { background: #2563EB; color: white; border: none; padding: 12px 24px; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%; margin: 10px 0; }
                        button:hover { background: #1d4ed8; }
                        button:disabled { background: #ccc; cursor: not-allowed; }
                        #result { margin-top: 20px; }
                        .small { font-size: 14px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>🏗️ Construction Tracker</h1>
                        <div class="success">
                            <strong>✅ Network Connection Working!</strong><br>
                            You can reach the server.
                        </div>
                    </div>

                    <div class="card">
                        <h2>🧪 Test API Authentication</h2>
                        <p class="small">Click the button below to test if the API key works:</p>
                        <button onclick="testAPI()" id="testBtn">Test API Connection</button>
                        <div id="result"></div>
                    </div>

                    <div class="card">
                        <h2>📱 Mobile App Settings</h2>
                        <p><strong>Server URL:</strong><br><code>$serverUrl</code></p>
                        <p><strong>API Key:</strong><br><code>c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c</code></p>
                        <p class="small">Copy these exact settings into your mobile app.</p>
                    </div>

                    <script>
                        const API_KEY = 'c3837ce4f3a62a5cafa92c99db47e26b0e9acaeeb0fc890b54b0060976217e5c';
                        const SERVER_URL = '$serverUrl';

                        async function testAPI() {
                            const resultDiv = document.getElementById('result');
                            const testBtn = document.getElementById('testBtn');

                            testBtn.disabled = true;
                            testBtn.textContent = 'Testing...';
                            resultDiv.innerHTML = '<div class="info">⏳ Testing API connection...</div>';

                            try {
                                const response = await fetch(SERVER_URL + '/works', {
                                    method: 'GET',
                                    headers: {
                                        'X-API-Key': API_KEY,
                                        'Content-Type': 'application/json'
                                    }
                                });

                                const data = await response.json();

                                if (response.ok) {
                                    resultDiv.innerHTML = `
                                        <div class="success">
                                            <strong>✅ SUCCESS!</strong><br><br>
                                            <strong>API Authentication Works!</strong><br><br>
                                            The API key is correct and the server is responding.<br><br>
                                            <strong>Data received:</strong><br>
                                            <code>${'$'}{JSON.stringify(data, null, 2)}</code><br><br>
                                            <strong>✓ Your mobile app should work with these exact settings!</strong>
                                        </div>
                                    `;
                                } else {
                                    resultDiv.innerHTML = `
                                        <div class="error">
                                            <strong>❌ API Error</strong><br><br>
                                            Status: ${'$'}{response.status}<br>
                                            ${'$'}{data.error || JSON.stringify(data)}<br><br>
                                            ${'$'}{response.status === 401 ?
                                                'The API key is incorrect or missing.' :
                                                'There was a server error.'}
                                        </div>
                                    `;
                                }
                            } catch (error) {
                                resultDiv.innerHTML = `
                                    <div class="error">
                                        <strong>❌ Connection Error</strong><br><br>
                                        ${'$'}{error.message}<br><br>
                                        Make sure the server is still running.
                                    </div>
                                `;
                            } finally {
                                testBtn.disabled = false;
                                testBtn.textContent = 'Test API Connection';
                            }
                        }
                    </script>
                </body>
                </html>
            """.trimIndent(), io.ktor.http.ContentType.Text.Html)
        }

        workRoutes(workRepo)
        roleRoutes(roleRepo)
        employeeRoutes(employeeRepo)
        dayAdjustmentRoutes(adjustmentRepo, payrollRepo, employeeRepo)
        monthlyPayrollRoutes(payrollRepo, employeeRepo)
    }
}