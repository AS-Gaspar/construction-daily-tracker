package org.gaspar.construction_daily_tracker

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.BeforeClass
import kotlin.test.*

class ApplicationTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    @Test
    fun testRoot() = testApplication {
        application {
            testModule()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Construction Daily Tracker API", response.bodyAsText())
    }
}