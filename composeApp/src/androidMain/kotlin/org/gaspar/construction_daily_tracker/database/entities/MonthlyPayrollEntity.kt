package org.gaspar.construction_daily_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monthly_payrolls",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["employeeId"])]
)
data class MonthlyPayrollEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val periodStartDate: String,
    val periodEndDate: String,
    val baseWorkdays: String,
    val finalWorkedDays: String,
    val totalPayment: String,
    val closedAt: Long? = null
)
