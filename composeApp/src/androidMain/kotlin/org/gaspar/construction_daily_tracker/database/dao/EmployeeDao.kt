package org.gaspar.construction_daily_tracker.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.gaspar.construction_daily_tracker.database.entities.EmployeeEntity

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees")
    fun getAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Int): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE workId = :workId")
    fun getByWorkId(workId: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE roleId = :roleId")
    fun getByRoleId(roleId: Int): Flow<List<EmployeeEntity>>

    @Insert
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity)

    @Delete
    suspend fun delete(employee: EmployeeEntity)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteById(id: Int)
}
