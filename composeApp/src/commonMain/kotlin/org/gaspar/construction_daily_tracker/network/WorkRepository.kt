package org.gaspar.construction_daily_tracker.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.gaspar.construction_daily_tracker.model.Work

/**
 * Repository for Work-related API calls.
 * Example implementation showing how to use the API client.
 */
class WorkRepository(private val client: HttpClient) {

    /**
     * Fetches all works from the API.
     */
    suspend fun getAllWorks(): List<Work> {
        return client.get("/works").body()
    }

    /**
     * Fetches a specific work by ID.
     */
    suspend fun getWorkById(id: Int): Work {
        return client.get("/works/$id").body()
    }

    /**
     * Creates a new work.
     */
    suspend fun createWork(name: String): Work {
        return client.post("/works") {
            setBody(mapOf("name" to name))
        }.body()
    }

    /**
     * Updates an existing work.
     */
    suspend fun updateWork(id: Int, name: String): Work {
        return client.put("/works/$id") {
            setBody(mapOf("name" to name))
        }.body()
    }

    /**
     * Deletes a work.
     */
    suspend fun deleteWork(id: Int) {
        client.delete("/works/$id")
    }
}
