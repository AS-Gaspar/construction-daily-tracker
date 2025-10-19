package org.gaspar.construction_daily_tracker.repository

import org.gaspar.construction_daily_tracker.TestDatabaseFactory
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupDatabase() {
            TestDatabaseFactory.init()
        }
    }

    private lateinit var repository: WorkRepository

    @Before
    fun setup() {
        TestDatabaseFactory.clean()
        repository = WorkRepository()
    }

    @Test
    fun `should create work with generated id`() {
        // Given: Nome de uma obra
        val workName = "Obra Central"

        // When: Criar obra
        val work = repository.create(workName)

        // Then: Obra deve ter ID e nome correto
        assertTrue(work.id > 0)
        assertEquals(workName, work.name)
    }

    @Test
    fun `should find work by id`() {
        // Given: Uma obra criada
        val created = repository.create("Obra Norte")

        // When: Buscar por ID
        val found = repository.findById(created.id)

        // Then: Deve encontrar a obra
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals(created.name, found.name)
    }

    @Test
    fun `should return null when work not found`() {
        // When: Buscar obra inexistente
        val found = repository.findById(999)

        // Then: Deve retornar null
        assertNull(found)
    }

    @Test
    fun `should list all works`() {
        // Given: Múltiplas obras criadas
        repository.create("Obra 1")
        repository.create("Obra 2")
        repository.create("Obra 3")

        // When: Listar todas
        val works = repository.findAll()

        // Then: Deve retornar todas as obras
        assertEquals(3, works.size)
    }

    @Test
    fun `should update work name`() {
        // Given: Uma obra criada
        val work = repository.create("Nome Antigo")

        // When: Atualizar nome
        val updated = repository.update(work.id, "Nome Novo")

        // Then: Atualização deve ser bem-sucedida
        assertTrue(updated)

        // E o nome deve estar atualizado
        val found = repository.findById(work.id)
        assertEquals("Nome Novo", found?.name)
    }

    @Test
    fun `should return false when updating non-existent work`() {
        // When: Tentar atualizar obra inexistente
        val updated = repository.update(999, "Nome")

        // Then: Deve retornar false
        assertEquals(false, updated)
    }

    @Test
    fun `should delete work`() {
        // Given: Uma obra criada
        val work = repository.create("Obra a Deletar")

        // When: Deletar obra
        val deleted = repository.delete(work.id)

        // Then: Deleção deve ser bem-sucedida
        assertTrue(deleted)

        // E a obra não deve mais existir
        assertNull(repository.findById(work.id))
    }

    @Test
    fun `should return false when deleting non-existent work`() {
        // When: Tentar deletar obra inexistente
        val deleted = repository.delete(999)

        // Then: Deve retornar false
        assertEquals(false, deleted)
    }
}
