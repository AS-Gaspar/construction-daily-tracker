package org.gaspar.construction_daily_tracker.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkDaysCalculatorTest {

    @Test
    fun `should calculate work days for simple week`() {
        // Given: Uma semana completa (segunda a sexta)
        val startDate = "2024-01-08" // Segunda
        val endDate = "2024-01-12"   // Sexta

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter 5 dias úteis
        assertEquals(5, workDays)
    }

    @Test
    fun `should exclude weekends from calculation`() {
        // Given: Período incluindo fim de semana
        val startDate = "2024-01-08" // Segunda
        val endDate = "2024-01-14"   // Domingo

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter apenas 5 dias úteis (seg a sex)
        assertEquals(5, workDays)
    }

    @Test
    fun `should calculate work days for monthly period from 6th to 5th`() {
        // Given: Período de 6 de outubro a 5 de novembro de 2024
        val startDate = "2024-10-06" // Domingo (não conta)
        val endDate = "2024-11-05"   // Terça

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter 22 dias úteis (segunda a sexta apenas)
        assertEquals(22, workDays)
    }

    @Test
    fun `should return 0 for weekend only period`() {
        // Given: Apenas sábado e domingo
        val startDate = "2024-01-06" // Sábado
        val endDate = "2024-01-07"   // Domingo

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter 0 dias úteis
        assertEquals(0, workDays)
    }

    @Test
    fun `should return 1 for single work day`() {
        // Given: Apenas uma segunda-feira
        val startDate = "2024-01-08" // Segunda
        val endDate = "2024-01-08"   // Segunda

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter 1 dia útil
        assertEquals(1, workDays)
    }

    @Test
    fun `should calculate current month start correctly when before day 6`() {
        // When: Buscar data de início do mês atual
        val monthStart = getCurrentMonthStart()

        // Then: Deve retornar uma data no formato yyyy-MM-dd
        assertTrue(monthStart.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))

        // E deve ser dia 6 de algum mês
        assertTrue(monthStart.endsWith("-06"))
    }

    @Test
    fun `should calculate current month end correctly`() {
        // When: Buscar data de fim do mês atual
        val monthEnd = getCurrentMonthEnd()

        // Then: Deve retornar uma data no formato yyyy-MM-dd
        assertTrue(monthEnd.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))

        // E deve ser dia 5 de algum mês
        assertTrue(monthEnd.endsWith("-05"))
    }

    @Test
    fun `should calculate work days for february leap year`() {
        // Given: Período de 6 de fevereiro a 5 de março de 2024 (ano bissexto)
        val startDate = "2024-02-06" // Terça
        val endDate = "2024-03-05"   // Terça

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve ter 21 dias úteis
        assertEquals(21, workDays)
    }

    @Test
    fun `should calculate work days across year boundary`() {
        // Given: Período de 6 de dezembro a 5 de janeiro
        val startDate = "2023-12-06" // Quarta
        val endDate = "2024-01-05"   // Sexta

        // When: Calcular dias úteis
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Deve contar apenas dias úteis (excluindo feriados hipotéticos)
        assertTrue(workDays > 0)
    }
}
