package org.gaspar.construction_daily_tracker.utils

/**
 * Calcula dias úteis (segunda a sexta) entre duas datas.
 * As datas devem estar no formato ISO 8601: yyyy-MM-dd
 *
 * @param startDate Data inicial (inclusiva)
 * @param endDate Data final (inclusiva)
 * @return Número de dias úteis no período
 */
expect fun calculateWorkDays(startDate: String, endDate: String): Int

/**
 * Retorna data no formato ISO 8601 (yyyy-MM-dd) para o dia 6 do mês atual
 */
expect fun getCurrentMonthStart(): String

/**
 * Retorna data no formato ISO 8601 (yyyy-MM-dd) para o dia 5 do próximo mês
 */
expect fun getCurrentMonthEnd(): String

/**
 * Verifica se hoje é dia 5 do mês (dia de fechamento)
 */
expect fun isClosingDay(): Boolean
