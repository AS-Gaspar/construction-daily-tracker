package org.gaspar.construction_daily_tracker.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.gaspar.construction_daily_tracker.database.entities.RoleEntity

@Dao
interface RoleDao {
    @Query("SELECT * FROM roles")
    fun getAll(): Flow<List<RoleEntity>>

    @Query("SELECT * FROM roles WHERE id = :id")
    suspend fun getById(id: Int): RoleEntity?

    @Insert
    suspend fun insert(role: RoleEntity): Long

    @Update
    suspend fun update(role: RoleEntity)

    @Delete
    suspend fun delete(role: RoleEntity)

    @Query("DELETE FROM roles WHERE id = :id")
    suspend fun deleteById(id: Int)
}
