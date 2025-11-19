package org.gaspar.construction_daily_tracker.repository

import kotlinx.coroutines.flow.first
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.database.entities.RoleEntity
import org.gaspar.construction_daily_tracker.model.Role

class LocalRoleRepository(private val database: AppDatabase) {
    private val roleDao = database.roleDao()

    suspend fun getAllRoles(): List<Role> {
        return roleDao.getAll().first().map { it.toModel() }
    }

    suspend fun getRoleById(id: Int): Role? {
        return roleDao.getById(id)?.toModel()
    }

    suspend fun createRole(title: String): Role {
        val id = roleDao.insert(RoleEntity(title = title))
        return Role(id.toInt(), title)
    }

    suspend fun updateRole(id: Int, title: String): Role {
        val entity = RoleEntity(id = id, title = title)
        roleDao.update(entity)
        return Role(id, title)
    }

    suspend fun deleteRole(id: Int) {
        roleDao.deleteById(id)
    }

    private fun RoleEntity.toModel() = Role(id, title)
}
