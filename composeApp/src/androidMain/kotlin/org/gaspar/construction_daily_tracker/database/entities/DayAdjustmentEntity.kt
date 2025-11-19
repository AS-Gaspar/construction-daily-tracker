package org.gaspar.construction_daily_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "day_adjustments",
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
data class DayAdjustmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val date: String,
    val adjustmentValue: String,
    val notes: String? = null
)
