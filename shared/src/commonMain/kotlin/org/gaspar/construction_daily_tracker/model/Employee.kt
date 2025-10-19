package org.gaspar.construction_daily_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class Work(
    val id: Int,
    val name: String
)

@Serializable
data class Role(
    val id: Int,
    val title: String
)

@Serializable
data class Employee(
    val id: Int,
    val name: String,
    val surname: String,
    val roleId: Int,
    val workId: Int,
    val dailyValue: String // Decimal como String para serialização
)
