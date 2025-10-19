package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoleRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private lateinit var repository: RoleRepository

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
        repository = RoleRepository()
    }

    @Test
    fun `should create role with generated id`() {
        // Given: Título de um cargo
        val roleTitle = "Pedreiro"

        // When: Criar cargo
        val role = repository.create(roleTitle)

        // Then: Cargo deve ter ID e título correto
        assertTrue(role.id > 0)
        assertEquals(roleTitle, role.title)
    }

    @Test
    fun `should find role by id`() {
        // Given: Um cargo criado
        val created = repository.create("Eletricista")

        // When: Buscar por ID
        val found = repository.findById(created.id)

        // Then: Deve encontrar o cargo
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals(created.title, found.title)
    }

    @Test
    fun `should return null when role not found`() {
        // When: Buscar cargo inexistente
        val found = repository.findById(999)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should list all roles`() {
        // Given: Múltiplos cargos criados
        repository.create("Pedreiro")
        repository.create("Servente")
        repository.create("Mestre de Obras")

        // When: Listar todos
        val roles = repository.findAll()

        // Then: Deve retornar todos os cargos
        assertEquals(3, roles.size)
    }

    @Test
    fun `should update role title`() {
        // Given: Um cargo criado
        val role = repository.create("Ajudante")

        // When: Atualizar título
        val updated = repository.update(role.id, "Auxiliar")

        // Then: Atualização deve ser bem-sucedida
        assertTrue(updated)

        // E o título deve estar atualizado
        val found = repository.findById(role.id)
        assertEquals("Auxiliar", found?.title)
    }

    @Test
    fun `should delete role`() {
        // Given: Um cargo criado
        val role = repository.create("Cargo Temporário")

        // When: Deletar cargo
        val deleted = repository.delete(role.id)

        // Then: Deleção deve ser bem-sucedida
        assertTrue(deleted)

        // E o cargo não deve mais existir
        assertNull(repository.findById(role.id))
    }
}
