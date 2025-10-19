package org.gaspar.construction_daily_tracker.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

/**
 * Plugin for API Key authentication.
 * Validates the X-API-Key header against the configured API key from environment variable.
 */
class ApiKeyAuthConfiguration {
    var apiKey: String = System.getenv("API_KEY") ?: "change-me-in-production"
}

val ApiKeyAuth = createApplicationPlugin(
    name = "ApiKeyAuth",
    createConfiguration = ::ApiKeyAuthConfiguration
) {
    val expectedApiKey = pluginConfig.apiKey

    onCall { call ->
        // Allow root endpoint without authentication
        if (call.request.local.uri == "/") {
            return@onCall
        }

        val requestApiKey = call.request.headers["X-API-Key"]

        if (requestApiKey == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing API key"))
            return@onCall
        }

        if (requestApiKey != expectedApiKey) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid API key"))
            return@onCall
        }
    }
}
