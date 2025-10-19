package org.gaspar.construction_daily_tracker

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import org.gaspar.construction_daily_tracker.model.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val TEST_API_KEY = "test-api-key"

private fun HttpRequestBuilder.withApiKey() {
    header("X-API-Key", TEST_API_KEY)
}

class PayrollFlowEndToEndTest {

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
    fun `complete payroll flow with adjustments`() = testApplication {
        application { testModule() }

        val workResponse = client.post("/works") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Obra Central"}""")
        }
        assertEquals(HttpStatusCode.Created, workResponse.status)
        val work = Json.decodeFromString<Work>(workResponse.bodyAsText())
        println("✓ Obra criada: ${work.name} (ID: ${work.id})")

        val roleResponse = client.post("/roles") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Pedreiro"}""")
        }
        assertEquals(HttpStatusCode.Created, roleResponse.status)
        val role = Json.decodeFromString<Role>(roleResponse.bodyAsText())
        println("✓ Cargo criado: ${role.title} (ID: ${role.id})")

        val employeeResponse = client.post("/employees") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "João",
                    "surname": "Silva",
                    "roleId": ${role.id},
                    "workId": ${work.id},
                    "dailyValue": "150.00"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, employeeResponse.status)
        val employee = Json.decodeFromString<Employee>(employeeResponse.bodyAsText())
        println("✓ Funcionário criado: ${employee.name} ${employee.surname} - R$${employee.dailyValue}/dia")

         val periodStart = "2024-10-06"
        val periodEnd = "2024-11-05"

        val payrollResponse = client.post("/monthly-payrolls") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "employeeId": ${employee.id},
                    "periodStartDate": "$periodStart",
                    "periodEndDate": "$periodEnd"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, payrollResponse.status)
        val payroll = Json.decodeFromString<MonthlyPayroll>(payrollResponse.bodyAsText())

        println("✓ Folha de pagamento criada:")
        println("  - Período: $periodStart a $periodEnd")
        println("  - Dias úteis base: ${payroll.baseWorkdays}")
        println("  - Dias trabalhados iniciais: ${payroll.finalWorkedDays}")
        println("  - Pagamento inicial: R$${payroll.totalPayment}")

        assertEquals("22", payroll.baseWorkdays.toBigDecimal().stripTrailingZeros().toPlainString()) // 22 dias úteis no período
        assertEquals("22", payroll.finalWorkedDays.toBigDecimal().stripTrailingZeros().toPlainString())
        assertTrue(BigDecimal("3300.00").compareTo(payroll.totalPayment.toBigDecimal()) == 0) // 22 * 150
        assertNull(payroll.closedAt)

        val saturdayResponse = client.post("/day-adjustments") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "employeeId": ${employee.id},
                    "date": "2024-10-12",
                    "adjustmentValue": "1.0",
                    "notes": "Sábado trabalhado - urgência na obra"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, saturdayResponse.status)
        val saturday = Json.decodeFromString<DayAdjustment>(saturdayResponse.bodyAsText())
        println("✓ Sábado trabalhado adicionado: ${saturday.date} (+${saturday.adjustmentValue} dia)")

        // Verificar atualização automática da folha
        var updatedPayroll = getPayroll(client, payroll.id)
        assertTrue(BigDecimal("23.0").compareTo(updatedPayroll.finalWorkedDays.toBigDecimal()) == 0) // 22 + 1
        assertTrue(BigDecimal("3450.00").compareTo(updatedPayroll.totalPayment.toBigDecimal()) == 0) // 23 * 150
        println("  - Dias atualizados: ${updatedPayroll.finalWorkedDays}")
        println("  - Pagamento atualizado: R$${updatedPayroll.totalPayment}")

        val absenceResponse = client.post("/day-adjustments") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "employeeId": ${employee.id},
                    "date": "2024-10-15",
                    "adjustmentValue": "-0.5",
                    "notes": "Falta meio período - consulta médica"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, absenceResponse.status)
        val absence = Json.decodeFromString<DayAdjustment>(absenceResponse.bodyAsText())
        println("✓ Falta adicionada: ${absence.date} (${absence.adjustmentValue} dia)")

        // Verificar atualização automática
        updatedPayroll = getPayroll(client, payroll.id)
        assertTrue(BigDecimal("22.5").compareTo(updatedPayroll.finalWorkedDays.toBigDecimal()) == 0) // 23 - 0.5
        assertTrue(BigDecimal("3375.00").compareTo(updatedPayroll.totalPayment.toBigDecimal()) == 0) // 22.5 * 150
        println("  - Dias atualizados: ${updatedPayroll.finalWorkedDays}")
        println("  - Pagamento atualizado: R$${updatedPayroll.totalPayment}")

              val saturday2Response = client.post("/day-adjustments") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "employeeId": ${employee.id},
                    "date": "2024-10-19",
                    "adjustmentValue": "1.0",
                    "notes": "Segundo sábado trabalhado"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, saturday2Response.status)
        println("✓ Segundo sábado adicionado: 2024-10-19 (+1.0 dia)")

        updatedPayroll = getPayroll(client, payroll.id)
        assertTrue(BigDecimal("23.5").compareTo(updatedPayroll.finalWorkedDays.toBigDecimal()) == 0) // 22.5 + 1
        assertTrue(BigDecimal("3525.00").compareTo(updatedPayroll.totalPayment.toBigDecimal()) == 0) // 23.5 * 150
        println("  - Dias finais: ${updatedPayroll.finalWorkedDays}")
        println("  - Pagamento final: R$${updatedPayroll.totalPayment}")

              val adjustmentsResponse = client.get("/day-adjustments/employee/${employee.id}") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.OK, adjustmentsResponse.status)
        val adjustments = Json.decodeFromString<List<DayAdjustment>>(adjustmentsResponse.bodyAsText())

        println("✓ Total de ajustes: ${adjustments.size}")
        assertEquals(3, adjustments.size)

        // Verificar ordenação (mais recente primeiro)
        assertEquals("2024-10-19", adjustments[0].date)
        assertEquals("2024-10-15", adjustments[1].date)
        assertEquals("2024-10-12", adjustments[2].date)

                val deleteResponse = client.delete("/day-adjustments/${saturday.id}") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)
        println("✓ Primeiro sábado removido")

        updatedPayroll = getPayroll(client, payroll.id)
        assertTrue(BigDecimal("22.5").compareTo(updatedPayroll.finalWorkedDays.toBigDecimal()) == 0) // 23.5 - 1 (removed first +1, still have second +1 and -0.5)
        assertTrue(BigDecimal("3375.00").compareTo(updatedPayroll.totalPayment.toBigDecimal()) == 0) // 22.5 * 150
        println("  - Dias após remoção: ${updatedPayroll.finalWorkedDays}")
        println("  - Pagamento após remoção: R$${updatedPayroll.totalPayment}")

             val activeResponse = client.get("/monthly-payrolls/employee/${employee.id}/active") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.OK, activeResponse.status)
        val activePayroll = Json.decodeFromString<MonthlyPayroll>(activeResponse.bodyAsText())
        assertEquals(payroll.id, activePayroll.id)
        assertNull(activePayroll.closedAt)
        println("✓ Folha ativa confirmada (ID: ${activePayroll.id})")

           val closeResponse = client.put("/monthly-payrolls/${payroll.id}/close") {
            withApiKey()
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, closeResponse.status)
        println("✓ Folha de pagamento fechada")

        val closedPayroll = getPayroll(client, payroll.id)
        assertNotNull(closedPayroll.closedAt)
        assertTrue(closedPayroll.closedAt!! > 0)
        println("  - Fechada em: ${closedPayroll.closedAt}")

        // Verificar que não há mais folha ativa
        val noActiveResponse = client.get("/monthly-payrolls/employee/${employee.id}/active") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.NotFound, noActiveResponse.status)
        println("✓ Confirmado que não há folha ativa")

             val nextPeriodStart = "2024-11-06"
        val nextPeriodEnd = "2024-12-05"

        val nextPayrollResponse = client.post("/monthly-payrolls") {
            withApiKey()
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "employeeId": ${employee.id},
                    "periodStartDate": "$nextPeriodStart",
                    "periodEndDate": "$nextPeriodEnd"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, nextPayrollResponse.status)
        val nextPayroll = Json.decodeFromString<MonthlyPayroll>(nextPayrollResponse.bodyAsText())

        println("✓ Nova folha criada para próximo mês:")
        println("  - Período: $nextPeriodStart a $nextPeriodEnd")
        println("  - Dias úteis base: ${nextPayroll.baseWorkdays}")
        println("  - Pagamento inicial: R$${nextPayroll.totalPayment}")

        assertNull(nextPayroll.closedAt)

          val historyResponse = client.get("/monthly-payrolls/employee/${employee.id}") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.OK, historyResponse.status)
        val history = Json.decodeFromString<List<MonthlyPayroll>>(historyResponse.bodyAsText())

        println("✓ Histórico de folhas: ${history.size} meses")
        assertEquals(2, history.size)

        assertEquals(nextPayroll.id, history[0].id)
        assertEquals(payroll.id, history[1].id)

        assertNotNull(history[1].closedAt)
        assertNull(history[0].closedAt)

          println("\n========================================")
        println("RESUMO DO MÊS FECHADO:")
        println("========================================")
        println("Funcionário: ${employee.name} ${employee.surname}")
        println("Período: ${closedPayroll.periodStartDate} a ${closedPayroll.periodEndDate}")
        println("Dias úteis base: ${closedPayroll.baseWorkdays}")
        println("Dias trabalhados final: ${closedPayroll.finalWorkedDays}")
        println("Valor diário: R$${employee.dailyValue}")
        println("TOTAL A PAGAR: R$${closedPayroll.totalPayment}")
        println("========================================")

        assertEquals("22", closedPayroll.baseWorkdays.toBigDecimal().stripTrailingZeros().toPlainString())
        assertEquals("22.5", closedPayroll.finalWorkedDays.toBigDecimal().stripTrailingZeros().toPlainString())
        assertTrue(BigDecimal("3375.00").compareTo(closedPayroll.totalPayment.toBigDecimal()) == 0)

        val expectedFinalDays = BigDecimal("22").add(BigDecimal("0.5"))
        assertTrue(expectedFinalDays.compareTo(closedPayroll.finalWorkedDays.toBigDecimal()) == 0)

        println("\n✅ TESTE END-TO-END COMPLETO COM SUCESSO!")
    }

    private suspend fun ApplicationTestBuilder.getPayroll(
        client: HttpClient,
        payrollId: Int
    ): MonthlyPayroll {
        val response = client.get("/monthly-payrolls/$payrollId") {
            withApiKey()
        }
        assertEquals(HttpStatusCode.OK, response.status)
        return Json.decodeFromString(response.bodyAsText())
    }
}
