package org.gaspar.construction_daily_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class DayAdjustment(
    val id: Int,
    val employeeId: Int,
    val date: String, // ISO 8601 format: yyyy-MM-dd
    val adjustmentValue: String, // Decimal como String, pode ser negativo ou positivo
    val notes: String? = null
)
