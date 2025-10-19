package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DayAdjustmentRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private lateinit var adjustmentRepo: DayAdjustmentRepository
    private lateinit var employeeRepo: EmployeeRepository
    private lateinit var workRepo: WorkRepository
    private lateinit var roleRepo: RoleRepository

    private var employeeId: Int = 0

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
        adjustmentRepo = DayAdjustmentRepository()
        employeeRepo = EmployeeRepository()
        workRepo = WorkRepository()
        roleRepo = RoleRepository()

        // Criar funcionário para os testes
        val work = workRepo.create("Obra Teste")
        val role = roleRepo.create("Pedreiro")
        val employee = employeeRepo.create("João", "Silva", role.id, work.id, BigDecimal("150.00"))
        employeeId = employee.id
    }

    @Test
    fun `should create positive adjustment for saturday`() {
        // Given: Dados de um sábado trabalhado
        val date = "2024-10-12"
        val adjustmentValue = BigDecimal("1.0")
        val notes = "Sábado trabalhado"

        // When: Criar ajuste positivo
        val adjustment = adjustmentRepo.create(employeeId, date, adjustmentValue, notes)

        // Then: Ajuste deve ter todos os campos corretos
        assertTrue(adjustment.id > 0)
        assertEquals(employeeId, adjustment.employeeId)
        assertEquals(date, adjustment.date)
        assertEquals("1.0", adjustment.adjustmentValue)
        assertEquals(notes, adjustment.notes)
    }

    @Test
    fun `should create negative adjustment for absence`() {
        // Given: Dados de uma falta
        val date = "2024-10-15"
        val adjustmentValue = BigDecimal("-1.0")
        val notes = "Falta justificada"

        // When: Criar ajuste negativo
        val adjustment = adjustmentRepo.create(employeeId, date, adjustmentValue, notes)

        // Then: Ajuste deve ter valor negativo
        assertEquals("-1.0", adjustment.adjustmentValue)
    }

    @Test
    fun `should create half day adjustment`() {
        // Given: Meio período trabalhado
        val adjustmentValue = BigDecimal("0.5")

        // When: Criar ajuste de meio dia
        val adjustment = adjustmentRepo.create(employeeId, "2024-10-12", adjustmentValue)

        // Then: Valor deve ser 0.5
        assertEquals("0.5", adjustment.adjustmentValue)
    }

    @Test
    fun `should create adjustment without notes`() {
        // When: Criar ajuste sem observações
        val adjustment = adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("-0.5"))

        // Then: Notes deve ser null
        assertNull(adjustment.notes)
    }

    @Test
    fun `should find adjustment by id`() {
        // Given: Um ajuste criado
        val created = adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("1.0"), "Teste")

        // When: Buscar por ID
        val found = adjustmentRepo.findById(created.id)

        // Then: Deve encontrar o ajuste
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals(created.employeeId, found.employeeId)
    }

    @Test
    fun `should return null when adjustment not found`() {
        // When: Buscar ajuste inexistente
        val found = adjustmentRepo.findById(999)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should find all adjustments by employee id`() {
        // Given: Múltiplos ajustes para um funcionário
        adjustmentRepo.create(employeeId, "2024-10-10", BigDecimal("1.0"))
        adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("-1.0"))
        adjustmentRepo.create(employeeId, "2024-10-19", BigDecimal("0.5"))

        // When: Buscar ajustes do funcionário
        val adjustments = adjustmentRepo.findByEmployeeId(employeeId)

        // Then: Deve retornar todos os ajustes
        assertEquals(3, adjustments.size)
        assertTrue(adjustments.all { it.employeeId == employeeId })
    }

    @Test
    fun `should order adjustments by date descending`() {
        // Given: Ajustes em datas diferentes
        adjustmentRepo.create(employeeId, "2024-10-10", BigDecimal("1.0"))
        adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("-1.0"))
        adjustmentRepo.create(employeeId, "2024-10-12", BigDecimal("0.5"))

        // When: Buscar ajustes
        val adjustments = adjustmentRepo.findByEmployeeId(employeeId)

        // Then: Deve estar ordenado por data decrescente
        assertEquals("2024-10-15", adjustments[0].date)
        assertEquals("2024-10-12", adjustments[1].date)
        assertEquals("2024-10-10", adjustments[2].date)
    }

    @Test
    fun `should find adjustments by date range`() {
        // Given: Ajustes em diferentes meses
        adjustmentRepo.create(employeeId, "2024-10-05", BigDecimal("1.0"))
        adjustmentRepo.create(employeeId, "2024-10-10", BigDecimal("-1.0"))
        adjustmentRepo.create(employeeId, "2024-10-20", BigDecimal("0.5"))
        adjustmentRepo.create(employeeId, "2024-11-05", BigDecimal("1.0"))

        // When: Buscar ajustes entre 06/10 e 05/11
        val adjustments = adjustmentRepo.findByEmployeeIdAndDateRange(
            employeeId,
            "2024-10-06",
            "2024-11-05"
        )

        // Then: Deve retornar apenas ajustes no período
        assertEquals(3, adjustments.size)
        assertTrue(adjustments.none { it.date == "2024-10-05" })
    }

    @Test
    fun `should order date range results by date ascending`() {
        // Given: Ajustes em ordem não cronológica
        adjustmentRepo.create(employeeId, "2024-10-20", BigDecimal("1.0"))
        adjustmentRepo.create(employeeId, "2024-10-10", BigDecimal("-1.0"))
        adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("0.5"))

        // When: Buscar por range
        val adjustments = adjustmentRepo.findByEmployeeIdAndDateRange(
            employeeId,
            "2024-10-01",
            "2024-10-31"
        )

        // Then: Deve estar ordenado por data crescente
        assertEquals("2024-10-10", adjustments[0].date)
        assertEquals("2024-10-15", adjustments[1].date)
        assertEquals("2024-10-20", adjustments[2].date)
    }

    @Test
    fun `should delete adjustment`() {
        // Given: Um ajuste criado
        val adjustment = adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("1.0"))

        // When: Deletar ajuste
        val deleted = adjustmentRepo.delete(adjustment.id)

        // Then: Deleção deve ser bem-sucedida
        assertTrue(deleted)

        // E o ajuste não deve mais existir
        assertNull(adjustmentRepo.findById(adjustment.id))
    }

    @Test
    fun `should not find adjustments from other employees`() {
        // Given: Dois funcionários com ajustes
        val work = workRepo.create("Obra 2")
        val role = roleRepo.create("Servente")
        val employee2 = employeeRepo.create("Maria", "Santos", role.id, work.id, BigDecimal("120.00"))

        adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("1.0"))
        adjustmentRepo.create(employee2.id, "2024-10-15", BigDecimal("-1.0"))

        // When: Buscar ajustes do primeiro funcionário
        val adjustments = adjustmentRepo.findByEmployeeId(employeeId)

        // Then: Deve retornar apenas ajustes do primeiro funcionário
        assertEquals(1, adjustments.size)
        assertEquals(employeeId, adjustments[0].employeeId)
    }

    @Test
    fun `should handle negative decimal values correctly`() {
        // Given: Meio dia de falta
        val adjustment = adjustmentRepo.create(employeeId, "2024-10-15", BigDecimal("-0.5"))

        // When: Buscar ajuste
        val found = adjustmentRepo.findById(adjustment.id)

        // Then: Valor negativo decimal deve ser preservado
        assertEquals("-0.5", found?.adjustmentValue?.toBigDecimal()?.stripTrailingZeros()?.toPlainString())
    }
}
