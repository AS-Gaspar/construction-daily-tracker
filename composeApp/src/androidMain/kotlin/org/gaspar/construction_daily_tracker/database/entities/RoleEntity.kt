package org.gaspar.construction_daily_tracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
)
