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

class EmployeeRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private lateinit var employeeRepo: EmployeeRepository
    private lateinit var workRepo: WorkRepository
    private lateinit var roleRepo: RoleRepository

    private var workId: Int = 0
    private var roleId: Int = 0

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
        employeeRepo = EmployeeRepository()
        workRepo = WorkRepository()
        roleRepo = RoleRepository()

        // Criar obra e cargo para os testes
        val work = workRepo.create("Obra Teste")
        val role = roleRepo.create("Pedreiro")
        workId = work.id
        roleId = role.id
    }

    @Test
    fun `should create employee with all required fields`() {
        // Given: Dados de um funcionário
        val name = "João"
        val surname = "Silva"
        val dailyValue = BigDecimal("150.00")

        // When: Criar funcionário
        val employee = employeeRepo.create(name, surname, roleId, workId, dailyValue)

        // Then: Funcionário deve ter todos os campos corretos
        assertTrue(employee.id > 0)
        assertEquals(name, employee.name)
        assertEquals(surname, employee.surname)
        assertEquals(roleId, employee.roleId)
        assertEquals(workId, employee.workId)
        assertEquals("150.00", employee.dailyValue)
    }

    @Test
    fun `should find employee by id`() {
        // Given: Um funcionário criado
        val created = employeeRepo.create("Maria", "Santos", roleId, workId, BigDecimal("200.00"))

        // When: Buscar por ID
        val found = employeeRepo.findById(created.id)

        // Then: Deve encontrar o funcionário
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Maria", found.name)
        assertEquals("Santos", found.surname)
    }

    @Test
    fun `should return null when employee not found`() {
        // When: Buscar funcionário inexistente
        val found = employeeRepo.findById(999)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should list all employees`() {
        // Given: Múltiplos funcionários criados
        employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))
        employeeRepo.create("Maria", "Santos", roleId, workId, BigDecimal("180.00"))
        employeeRepo.create("Pedro", "Oliveira", roleId, workId, BigDecimal("160.00"))

        // When: Listar todos
        val employees = employeeRepo.findAll()

        // Then: Deve retornar todos os funcionários
        assertEquals(3, employees.size)
    }

    @Test
    fun `should find employees by work id`() {
        // Given: Funcionários em obras diferentes
        val work2 = workRepo.create("Obra 2")
        employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))
        employeeRepo.create("Maria", "Santos", roleId, workId, BigDecimal("180.00"))
        employeeRepo.create("Pedro", "Oliveira", roleId, work2.id, BigDecimal("160.00"))

        // When: Buscar funcionários da primeira obra
        val employees = employeeRepo.findByWorkId(workId)

        // Then: Deve retornar apenas funcionários da primeira obra
        assertEquals(2, employees.size)
        assertTrue(employees.all { it.workId == workId })
    }

    @Test
    fun `should find employees by role id`() {
        // Given: Funcionários com cargos diferentes
        val role2 = roleRepo.create("Servente")
        employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))
        employeeRepo.create("Maria", "Santos", role2.id, workId, BigDecimal("120.00"))
        employeeRepo.create("Pedro", "Oliveira", roleId, workId, BigDecimal("160.00"))

        // When: Buscar funcionários do primeiro cargo
        val employees = employeeRepo.findByRoleId(roleId)

        // Then: Deve retornar apenas funcionários do primeiro cargo
        assertEquals(2, employees.size)
        assertTrue(employees.all { it.roleId == roleId })
    }

    @Test
    fun `should update employee name`() {
        // Given: Um funcionário criado
        val employee = employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))

        // When: Atualizar nome
        val updated = employeeRepo.update(employee.id, name = "José")

        // Then: Atualização deve ser bem-sucedida
        assertTrue(updated)

        // E o nome deve estar atualizado
        val found = employeeRepo.findById(employee.id)
        assertEquals("José", found?.name)
        assertEquals("Silva", found?.surname) // Sobrenome não mudou
    }

    @Test
    fun `should update employee daily value`() {
        // Given: Um funcionário criado
        val employee = employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))

        // When: Atualizar valor diário
        val newDailyValue = BigDecimal("200.00")
        val updated = employeeRepo.update(employee.id, dailyValue = newDailyValue)

        // Then: Atualização deve ser bem-sucedida
        assertTrue(updated)

        // E o valor deve estar atualizado
        val found = employeeRepo.findById(employee.id)
        assertEquals("200.00", found?.dailyValue)
    }

    @Test
    fun `should update multiple employee fields at once`() {
        // Given: Um funcionário criado
        val employee = employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))
        val newRole = roleRepo.create("Mestre de Obras")

        // When: Atualizar múltiplos campos
        val updated = employeeRepo.update(
            employee.id,
            name = "José",
            surname = "Santos",
            roleId = newRole.id,
            dailyValue = BigDecimal("300.00")
        )

        // Then: Todas as atualizações devem ser aplicadas
        assertTrue(updated)

        val found = employeeRepo.findById(employee.id)
        assertEquals("José", found?.name)
        assertEquals("Santos", found?.surname)
        assertEquals(newRole.id, found?.roleId)
        assertEquals("300.00", found?.dailyValue)
        assertEquals(workId, found?.workId) // Work não mudou
    }

    @Test
    fun `should delete employee`() {
        // Given: Um funcionário criado
        val employee = employeeRepo.create("João", "Silva", roleId, workId, BigDecimal("150.00"))

        // When: Deletar funcionário
        val deleted = employeeRepo.delete(employee.id)

        // Then: Deleção deve ser bem-sucedida
        assertTrue(deleted)

        // E o funcionário não deve mais existir
        assertNull(employeeRepo.findById(employee.id))
    }

    @Test
    fun `should handle decimal values correctly`() {
        // Given: Funcionário com valor decimal preciso
        val dailyValue = BigDecimal("123.45")
        val employee = employeeRepo.create("João", "Silva", roleId, workId, dailyValue)

        // When: Buscar funcionário
        val found = employeeRepo.findById(employee.id)

        // Then: Valor decimal deve ser preservado
        assertEquals("123.45", found?.dailyValue)
    }
}
