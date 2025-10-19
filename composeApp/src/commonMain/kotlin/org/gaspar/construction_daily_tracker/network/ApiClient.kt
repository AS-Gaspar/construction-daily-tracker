package org.gaspar.construction_daily_tracker.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Configures and provides the HTTP client for API calls.
 */
object ApiClient {

    /**
     * Creates a configured HTTP client with API key authentication.
     * @param baseUrl The base URL of the API server
     * @param apiKey The API key for authentication
     */
    fun create(baseUrl: String, apiKey: String): HttpClient {
        return HttpClient {
            // Install JSON serialization
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }

            // Set default request configuration
            defaultRequest {
                url(baseUrl)
                header("X-API-Key", apiKey)
                header("Content-Type", "application/json")
            }

            // Configure timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 30_000
            }
        }
    }
}
