package org.gaspar.construction_daily_tracker.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.gaspar.construction_daily_tracker.database.entities.WorkEntity

@Dao
interface WorkDao {
    @Query("SELECT * FROM works")
    fun getAll(): Flow<List<WorkEntity>>

    @Query("SELECT * FROM works WHERE id = :id")
    suspend fun getById(id: Int): WorkEntity?

    @Insert
    suspend fun insert(work: WorkEntity): Long

    @Update
    suspend fun update(work: WorkEntity)

    @Delete
    suspend fun delete(work: WorkEntity)

    @Query("DELETE FROM works WHERE id = :id")
    suspend fun deleteById(id: Int)
}
