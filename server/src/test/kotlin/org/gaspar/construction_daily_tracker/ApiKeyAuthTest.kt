package org.gaspar.construction_daily_tracker

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

class ApiKeyAuthTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
    }

    @Test
    fun `should return 401 when API key is missing`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/works")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `should return 401 when API key is invalid`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/works") {
            header("X-API-Key", "invalid-key")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `should allow access with valid API key`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/works") {
            header("X-API-Key", "test-api-key")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `should protect POST endpoints`() = testApplication {
        application {
            testModule()
        }

        // Without API key
        val responseWithoutKey = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test Work"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, responseWithoutKey.status)

        // With valid API key
        val responseWithKey = client.post("/works") {
            header("X-API-Key", "test-api-key")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test Work"}""")
        }
        assertEquals(HttpStatusCode.Created, responseWithKey.status)
    }

    @Test
    fun `should protect PUT endpoints`() = testApplication {
        application {
            testModule()
        }

        // Create a work first
        client.post("/works") {
            header("X-API-Key", "test-api-key")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test Work"}""")
        }

        // Try to update without API key
        val responseWithoutKey = client.put("/works/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Updated Work"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, responseWithoutKey.status)
    }

    @Test
    fun `should protect DELETE endpoints`() = testApplication {
        application {
            testModule()
        }

        // Create a work first
        client.post("/works") {
            header("X-API-Key", "test-api-key")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test Work"}""")
        }

        // Try to delete without API key
        val responseWithoutKey = client.delete("/works/1")
        assertEquals(HttpStatusCode.Unauthorized, responseWithoutKey.status)
    }

    @Test
    fun `root endpoint should not require authentication`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
