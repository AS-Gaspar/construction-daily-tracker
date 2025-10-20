package org.gaspar.construction_daily_tracker.auth

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

/**
 * Plugin for API Key authentication.
 * Validates the X-API-Key header against the configured API key from environment variable.
 */
class ApiKeyAuthConfiguration {
    var apiKey: String? = null
}

val ApiKeyAuth = createApplicationPlugin(
    name = "ApiKeyAuth",
    createConfiguration = ::ApiKeyAuthConfiguration
) {
    val dotenv = dotenv {
        filename = ".env.local"
        ignoreIfMissing = true
    }
    val expectedApiKey = pluginConfig.apiKey ?: dotenv["API_KEY"] ?: "change-me-in-production"

    onCall { call ->
        val path = call.request.local.uri

        // Allow root and test endpoints without authentication
        if (path == "/" || path == "/test") {
            call.application.environment.log.info("API Auth: Allowing unauthenticated access to $path")
            return@onCall
        }

        val requestApiKey = call.request.headers["X-API-Key"]
        val maskedKey = requestApiKey?.let {
            if (it.length > 8) "${it.take(4)}...${it.takeLast(4)}" else "***"
        }

        call.application.environment.log.info("API Auth: Path=$path, API-Key=${maskedKey ?: "MISSING"}")

        if (requestApiKey == null) {
            call.application.environment.log.warn("API Auth: REJECTED - Missing API key for $path")
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing API key"))
            return@onCall
        }

        if (requestApiKey != expectedApiKey) {
            call.application.environment.log.warn("API Auth: REJECTED - Invalid API key for $path (received: $maskedKey)")
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid API key"))
            return@onCall
        }

        call.application.environment.log.info("API Auth: ACCEPTED - Valid API key for $path")
    }
}
