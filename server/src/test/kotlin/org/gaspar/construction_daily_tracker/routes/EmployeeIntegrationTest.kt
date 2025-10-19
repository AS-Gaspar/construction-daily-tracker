package org.gaspar.construction_daily_tracker.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.gaspar.construction_daily_tracker.testModule
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.Role
import org.gaspar.construction_daily_tracker.model.Work
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmployeeIntegrationTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private var workId: Int = 0
    private var roleId: Int = 0

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
    }

    private suspend fun ApplicationTestBuilder.createWorkAndRole() {
        // Criar obra
        val workResponse = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra Teste"}""")
        }
        val work = Json.decodeFromString<Work>(workResponse.bodyAsText())
        workId = work.id

        // Criar cargo
        val roleResponse = client.post("/roles") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Pedreiro"}""")
        }
        val role = Json.decodeFromString<Role>(roleResponse.bodyAsText())
        roleId = role.id
    }

    @Test
    fun `POST employees should create new employee`() = testApplication {
        application { testModule() }
        // Given: Obra e cargo existentes
        createWorkAndRole()

        // When: Criar funcionário
        val response = client.post("/employees") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "João",
                    "surname": "Silva",
                    "roleId": $roleId,
                    "workId": $workId,
                    "dailyValue": "150.00"
                }
            """.trimIndent())
        }

        // Then: Deve retornar 201 Created
        assertEquals(HttpStatusCode.Created, response.status)

        val employee = Json.decodeFromString<Employee>(response.bodyAsText())
        assertTrue(employee.id > 0)
        assertEquals("João", employee.name)
        assertEquals("Silva", employee.surname)
        assertEquals("150.00", employee.dailyValue)
    }

    @Test
    fun `GET employees by work should filter correctly`() = testApplication {
        application { testModule() }
        // Given: Funcionários em obras diferentes
        createWorkAndRole()

        val work2Response = client.post("/works") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra 2"}""")
        }
        val work2 = Json.decodeFromString<Work>(work2Response.bodyAsText())

        // Criar funcionários na primeira obra
        repeat(2) { i ->
            client.post("/employees") {
                contentType(ContentType.Application.Json)
                setBody("""
                    {
                        "name": "Funcionário",
                        "surname": "Obra1_$i",
                        "roleId": $roleId,
                        "workId": $workId,
                        "dailyValue": "150.00"
                    }
                """.trimIndent())
            }
        }

        // Criar funcionário na segunda obra
        client.post("/employees") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "Funcionário",
                    "surname": "Obra2",
                    "roleId": $roleId,
                    "workId": ${work2.id},
                    "dailyValue": "150.00"
                }
            """.trimIndent())
        }

        // When: Buscar funcionários da primeira obra
        val response = client.get("/employees/work/$workId")

        // Then: Deve retornar apenas funcionários da primeira obra
        assertEquals(HttpStatusCode.OK, response.status)
        val employees = Json.decodeFromString<List<Employee>>(response.bodyAsText())
        assertEquals(2, employees.size)
        assertTrue(employees.all { it.workId == workId })
    }

    @Test
    fun `PUT employees should update daily value`() = testApplication {
        application { testModule() }
        // Given: Um funcionário criado
        createWorkAndRole()

        val createResponse = client.post("/employees") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "João",
                    "surname": "Silva",
                    "roleId": $roleId,
                    "workId": $workId,
                    "dailyValue": "150.00"
                }
            """.trimIndent())
        }
        val created = Json.decodeFromString<Employee>(createResponse.bodyAsText())

        // When: Atualizar valor diário
        val response = client.put("/employees/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"dailyValue": "200.00"}""")
        }

        // Then: Deve retornar 200 OK
        assertEquals(HttpStatusCode.OK, response.status)

        // E o valor deve estar atualizado
        val getResponse = client.get("/employees/${created.id}")
        val updated = Json.decodeFromString<Employee>(getResponse.bodyAsText())
        assertEquals("200.00", updated.dailyValue)
    }

    @Test
    fun `POST employees with invalid daily value should return 400`() = testApplication {
        application { testModule() }
        // Given: Obra e cargo existentes
        createWorkAndRole()

        // When: Tentar criar funcionário com valor inválido
        val response = client.post("/employees") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "João",
                    "surname": "Silva",
                    "roleId": $roleId,
                    "workId": $workId,
                    "dailyValue": "invalid"
                }
            """.trimIndent())
        }

        // Then: Deve retornar 400 Bad Request
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
