package org.gaspar.construction_daily_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
)

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
)
