package org.gaspar.construction_daily_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class MonthlyPayroll(
    val id: Int,
    val employeeId: Int,
    val periodStartDate: String, // ISO 8601: yyyy-MM-dd (dia 6)
    val periodEndDate: String,   // ISO 8601: yyyy-MM-dd (dia 5 do próximo mês)
    val baseWorkdays: String,    // Decimal como String - dias úteis base do período
    val finalWorkedDays: String, // Decimal como String - dias finais após ajustes
    val totalPayment: String,    // Decimal como String - pagamento total
    val closedAt: Long? = null   // timestamp em milissegundos
)
