package org.gaspar.construction_daily_tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.gaspar.construction_daily_tracker.database.dao.*
import org.gaspar.construction_daily_tracker.database.entities.*

@Database(
    entities = [
        WorkEntity::class,
        RoleEntity::class,
        EmployeeEntity::class,
        DayAdjustmentEntity::class,
        MonthlyPayrollEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
    abstract fun roleDao(): RoleDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun dayAdjustmentDao(): DayAdjustmentDao
    abstract fun monthlyPayrollDao(): MonthlyPayrollDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "construction_daily_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
