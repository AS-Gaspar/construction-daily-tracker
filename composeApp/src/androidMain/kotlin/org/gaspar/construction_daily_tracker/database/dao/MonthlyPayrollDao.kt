package org.gaspar.construction_daily_tracker.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.gaspar.construction_daily_tracker.database.entities.MonthlyPayrollEntity

@Dao
interface MonthlyPayrollDao {
    @Query("SELECT * FROM monthly_payrolls")
    fun getAll(): Flow<List<MonthlyPayrollEntity>>

    @Query("SELECT * FROM monthly_payrolls WHERE id = :id")
    suspend fun getById(id: Int): MonthlyPayrollEntity?

    @Query("SELECT * FROM monthly_payrolls WHERE employeeId = :employeeId")
    fun getByEmployeeId(employeeId: Int): Flow<List<MonthlyPayrollEntity>>

    @Insert
    suspend fun insert(payroll: MonthlyPayrollEntity): Long

    @Update
    suspend fun update(payroll: MonthlyPayrollEntity)

    @Delete
    suspend fun delete(payroll: MonthlyPayrollEntity)

    @Query("DELETE FROM monthly_payrolls WHERE id = :id")
    suspend fun deleteById(id: Int)
}
