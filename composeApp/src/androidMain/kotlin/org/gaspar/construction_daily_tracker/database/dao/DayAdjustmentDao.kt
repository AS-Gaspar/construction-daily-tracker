package org.gaspar.construction_daily_tracker.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.gaspar.construction_daily_tracker.database.entities.DayAdjustmentEntity

@Dao
interface DayAdjustmentDao {
    @Query("SELECT * FROM day_adjustments")
    fun getAll(): Flow<List<DayAdjustmentEntity>>

    @Query("SELECT * FROM day_adjustments WHERE id = :id")
    suspend fun getById(id: Int): DayAdjustmentEntity?

    @Query("SELECT * FROM day_adjustments WHERE employeeId = :employeeId")
    fun getByEmployeeId(employeeId: Int): Flow<List<DayAdjustmentEntity>>

    @Insert
    suspend fun insert(dayAdjustment: DayAdjustmentEntity): Long

    @Update
    suspend fun update(dayAdjustment: DayAdjustmentEntity)

    @Delete
    suspend fun delete(dayAdjustment: DayAdjustmentEntity)

    @Query("DELETE FROM day_adjustments WHERE id = :id")
    suspend fun deleteById(id: Int)
}
