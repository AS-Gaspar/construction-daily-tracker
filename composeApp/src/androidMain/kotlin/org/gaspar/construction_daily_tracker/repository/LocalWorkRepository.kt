package org.gaspar.construction_daily_tracker.repository

import kotlinx.coroutines.flow.first
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.database.entities.WorkEntity
import org.gaspar.construction_daily_tracker.model.Work

class LocalWorkRepository(private val database: AppDatabase) {
    private val workDao = database.workDao()

    suspend fun getAllWorks(): List<Work> {
        return workDao.getAll().first().map { it.toModel() }
    }

    suspend fun getWorkById(id: Int): Work? {
        return workDao.getById(id)?.toModel()
    }

    suspend fun createWork(name: String): Work {
        val id = workDao.insert(WorkEntity(name = name))
        return Work(id.toInt(), name)
    }

    suspend fun updateWork(id: Int, name: String): Work {
        val entity = WorkEntity(id = id, name = name)
        workDao.update(entity)
        return Work(id, name)
    }

    suspend fun deleteWork(id: Int) {
        workDao.deleteById(id)
    }

    private fun WorkEntity.toModel() = Work(id, name)
}
