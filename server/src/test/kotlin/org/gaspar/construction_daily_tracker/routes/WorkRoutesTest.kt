package org.gaspar.construction_daily_tracker.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.gaspar.construction_daily_tracker.testModule
import org.gaspar.construction_daily_tracker.model.Work
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkRoutesTest {

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
    fun `GET works should return empty list initially`() = testApplication {
        application {
            testModule()
        }
        // When: Buscar obras sem criar nenhuma
        val response = client.get("/works")

        // Then: Deve retornar lista vazia
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `POST works should create new work`() = testApplication {
        application { testModule() }
        // Given: Dados de uma nova obra
        val requestBody = """{"name":"Obra Central"}"""

        // When: Criar obra
        val response = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        // Then: Deve retornar 201 Created
        assertEquals(HttpStatusCode.Created, response.status)

        // E deve retornar a obra criada
        val work = Json.decodeFromString<Work>(response.bodyAsText())
        assertTrue(work.id > 0)
        assertEquals("Obra Central", work.name)
    }

    @Test
    fun `GET works should return all created works`() = testApplication {
        application { testModule() }
        // Given: Múltiplas obras criadas
        client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra 1"}""")
        }
        client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra 2"}""")
        }

        // When: Listar obras
        val response = client.get("/works")

        // Then: Deve retornar todas as obras
        assertEquals(HttpStatusCode.OK, response.status)
        val works = Json.decodeFromString<List<Work>>(response.bodyAsText())
        assertEquals(2, works.size)
    }

    @Test
    fun `GET works by id should return specific work`() = testApplication {
        application { testModule() }
        // Given: Uma obra criada
        val createResponse = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra Específica"}""")
        }
        val created = Json.decodeFromString<Work>(createResponse.bodyAsText())

        // When: Buscar obra por ID
        val response = client.get("/works/${created.id}")

        // Then: Deve retornar a obra
        assertEquals(HttpStatusCode.OK, response.status)
        val work = Json.decodeFromString<Work>(response.bodyAsText())
        assertEquals(created.id, work.id)
        assertEquals("Obra Específica", work.name)
    }

    @Test
    fun `GET works by invalid id should return 404`() = testApplication {
        application { testModule() }
        // When: Buscar obra inexistente
        val response = client.get("/works/999")

        // Then: Deve retornar 404
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT works should update work name`() = testApplication {
        application { testModule() }
        // Given: Uma obra criada
        val createResponse = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Nome Antigo"}""")
        }
        val created = Json.decodeFromString<Work>(createResponse.bodyAsText())

        // When: Atualizar nome
        val response = client.put("/works/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Nome Novo"}""")
        }

        // Then: Deve retornar 200 OK
        assertEquals(HttpStatusCode.OK, response.status)

        // E o nome deve estar atualizado
        val getResponse = client.get("/works/${created.id}")
        val updated = Json.decodeFromString<Work>(getResponse.bodyAsText())
        assertEquals("Nome Novo", updated.name)
    }

    @Test
    fun `DELETE works should remove work`() = testApplication {
        application { testModule() }
        // Given: Uma obra criada
        val createResponse = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra a Deletar"}""")
        }
        val created = Json.decodeFromString<Work>(createResponse.bodyAsText())

        // When: Deletar obra
        val deleteResponse = client.delete("/works/${created.id}")

        // Then: Deve retornar 200 OK
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // E a obra não deve mais existir
        val getResponse = client.get("/works/${created.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }
}
