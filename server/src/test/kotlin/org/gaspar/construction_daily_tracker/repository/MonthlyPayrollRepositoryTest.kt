package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.*

class MonthlyPayrollRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private lateinit var payrollRepo: MonthlyPayrollRepository
    private lateinit var employeeRepo: EmployeeRepository
    private lateinit var workRepo: WorkRepository
    private lateinit var roleRepo: RoleRepository

    private var employeeId: Int = 0

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
        payrollRepo = MonthlyPayrollRepository()
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
    fun `should create payroll with all required fields`() {
        // Given: Dados de uma folha de pagamento
        val startDate = "2024-10-06"
        val endDate = "2024-11-05"
        val baseWorkdays = BigDecimal("22")
        val finalWorkedDays = BigDecimal("22")
        val totalPayment = BigDecimal("3300.00")

        // When: Criar folha
        val payroll = payrollRepo.create(
            employeeId,
            startDate,
            endDate,
            baseWorkdays,
            finalWorkedDays,
            totalPayment
        )

        // Then: Folha deve ter todos os campos corretos
        assertTrue(payroll.id > 0)
        assertEquals(employeeId, payroll.employeeId)
        assertEquals(startDate, payroll.periodStartDate)
        assertEquals(endDate, payroll.periodEndDate)
        assertEquals("22", payroll.baseWorkdays)
        assertEquals("22", payroll.finalWorkedDays)
        assertEquals("3300.00", payroll.totalPayment)
        assertNull(payroll.closedAt)
    }

    @Test
    fun `should find payroll by id`() {
        // Given: Uma folha criada
        val created = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            BigDecimal("22"),
            BigDecimal("22"),
            BigDecimal("3300.00")
        )

        // When: Buscar por ID
        val found = payrollRepo.findById(created.id)

        // Then: Deve encontrar a folha
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals(created.employeeId, found.employeeId)
    }

    @Test
    fun `should return null when payroll not found`() {
        // When: Buscar folha inexistente
        val found = payrollRepo.findById(999)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should find all payrolls by employee id`() {
        // Given: Múltiplas folhas para um funcionário
        payrollRepo.create(employeeId, "2024-09-06", "2024-10-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.create(employeeId, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.create(employeeId, "2024-11-06", "2024-12-05", BigDecimal("21"), BigDecimal("21"), BigDecimal("3150.00"))

        // When: Buscar folhas do funcionário
        val payrolls = payrollRepo.findByEmployeeId(employeeId)

        // Then: Deve retornar todas as folhas
        assertEquals(3, payrolls.size)
        assertTrue(payrolls.all { it.employeeId == employeeId })
    }

    @Test
    fun `should order payrolls by start date descending`() {
        // Given: Folhas em ordem não cronológica
        payrollRepo.create(employeeId, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.create(employeeId, "2024-09-06", "2024-10-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.create(employeeId, "2024-11-06", "2024-12-05", BigDecimal("21"), BigDecimal("21"), BigDecimal("3150.00"))

        // When: Buscar folhas
        val payrolls = payrollRepo.findByEmployeeId(employeeId)

        // Then: Deve estar ordenado por data decrescente (mais recente primeiro)
        assertEquals("2024-11-06", payrolls[0].periodStartDate)
        assertEquals("2024-10-06", payrolls[1].periodStartDate)
        assertEquals("2024-09-06", payrolls[2].periodStartDate)
    }

    @Test
    fun `should find active payroll for employee`() {
        // Given: Folhas ativas e fechadas
        val closed = payrollRepo.create(employeeId, "2024-09-06", "2024-10-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.close(closed.id)

        val active = payrollRepo.create(employeeId, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))

        // When: Buscar folha ativa
        val found = payrollRepo.findActiveByEmployeeId(employeeId)

        // Then: Deve retornar apenas a folha ativa
        assertNotNull(found)
        assertEquals(active.id, found.id)
        assertNull(found.closedAt)
    }

    @Test
    fun `should return null when no active payroll exists`() {
        // Given: Apenas folha fechada
        val payroll = payrollRepo.create(employeeId, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))
        payrollRepo.close(payroll.id)

        // When: Buscar folha ativa
        val found = payrollRepo.findActiveByEmployeeId(employeeId)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should find all active payrolls`() {
        // Given: Múltiplos funcionários com folhas ativas e fechadas
        val work = workRepo.create("Obra 2")
        val role = roleRepo.create("Servente")
        val employee2 = employeeRepo.create("Maria", "Santos", role.id, work.id, BigDecimal("120.00"))

        payrollRepo.create(employeeId, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("3300.00"))

        val closed = payrollRepo.create(employee2.id, "2024-09-06", "2024-10-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("2640.00"))
        payrollRepo.close(closed.id)

        payrollRepo.create(employee2.id, "2024-10-06", "2024-11-05", BigDecimal("22"), BigDecimal("22"), BigDecimal("2640.00"))

        // When: Buscar todas as folhas ativas
        val active = payrollRepo.findAllActive()

        // Then: Deve retornar apenas folhas ativas
        assertEquals(2, active.size)
        assertTrue(active.all { it.closedAt == null })
    }

    @Test
    fun `should update final worked days and total payment`() {
        // Given: Uma folha criada
        val payroll = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            BigDecimal("22"),
            BigDecimal("22"),
            BigDecimal("3300.00")
        )

        // When: Atualizar dias trabalhados (após ajustes)
        val newFinalDays = BigDecimal("22.5")
        val newTotalPayment = BigDecimal("3375.00")
        val updated = payrollRepo.updateFinalWorkedDays(payroll.id, newFinalDays, newTotalPayment)

        // Then: Atualização deve ser bem-sucedida
        assertTrue(updated)

        // E os valores devem estar atualizados
        val found = payrollRepo.findById(payroll.id)
        assertEquals("22", found?.baseWorkdays?.toBigDecimal()?.stripTrailingZeros()?.toPlainString()) // base não muda
        assertEquals("22.5", found?.finalWorkedDays?.toBigDecimal()?.stripTrailingZeros()?.toPlainString())
        assertTrue(BigDecimal("3375.00").compareTo(found?.totalPayment?.toBigDecimal()) == 0)
    }

    @Test
    fun `should close payroll with timestamp`() {
        // Given: Uma folha ativa
        val payroll = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            BigDecimal("22"),
            BigDecimal("22"),
            BigDecimal("3300.00")
        )

        // When: Fechar folha
        val beforeClose = System.currentTimeMillis()
        val closed = payrollRepo.close(payroll.id)
        val afterClose = System.currentTimeMillis()

        // Then: Fechamento deve ser bem-sucedido
        assertTrue(closed)

        // E deve ter timestamp
        val found = payrollRepo.findById(payroll.id)
        assertNotNull(found?.closedAt)
        assertTrue(found!!.closedAt!! >= beforeClose)
        assertTrue(found.closedAt!! <= afterClose)
    }

    @Test
    fun `should not find closed payroll in active search`() {
        // Given: Folha fechada
        val payroll = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            BigDecimal("22"),
            BigDecimal("22"),
            BigDecimal("3300.00")
        )
        payrollRepo.close(payroll.id)

        // When: Buscar folhas ativas
        val active = payrollRepo.findAllActive()

        // Then: Não deve incluir a folha fechada
        assertEquals(0, active.size)
    }

    @Test
    fun `should calculate payment correctly with adjustments`() {
        // Given: Base de 22 dias úteis
        val baseWorkdays = BigDecimal("22")
        // Ajustes: +1 sábado, -0.5 falta = 22.5 dias finais
        val finalWorkedDays = BigDecimal("22.5")
        // Pagamento: 22.5 * 150 = 3375
        val totalPayment = BigDecimal("3375.00")

        // When: Criar folha com ajustes aplicados
        val payroll = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            baseWorkdays,
            finalWorkedDays,
            totalPayment
        )

        // Then: Valores devem estar corretos
        assertEquals("22", payroll.baseWorkdays)
        assertEquals("22.5", payroll.finalWorkedDays)
        assertTrue(BigDecimal("3375.00").compareTo(payroll.totalPayment.toBigDecimal()) == 0)
    }

    @Test
    fun `should handle decimal precision in payments`() {
        // Given: Cálculo com decimais
        val finalDays = BigDecimal("22.5")
        val dailyValue = BigDecimal("150.00")
        val expectedPayment = finalDays.multiply(dailyValue) // 3375.00

        // When: Criar folha
        val payroll = payrollRepo.create(
            employeeId,
            "2024-10-06",
            "2024-11-05",
            BigDecimal("22"),
            finalDays,
            expectedPayment
        )

        // Then: Precisão decimal deve ser mantida
        assertEquals("22.5", payroll.finalWorkedDays)
        assertTrue(BigDecimal("3375.00").compareTo(payroll.totalPayment.toBigDecimal()) == 0)
    }
}
