package org.gaspar.construction_daily_tracker.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "employees",
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["id"],
            childColumns = ["roleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["workId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["roleId"]), Index(value = ["workId"])]
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val surname: String,
    val roleId: Int,
    val workId: Int? = null,
    val dailyValue: String
)
