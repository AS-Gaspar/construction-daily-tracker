package org.gaspar.construction_daily_tracker.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

actual fun calculateWorkDays(startDate: String, endDate: String): Int {
    val start = LocalDate.parse(startDate, dateFormatter)
    val end = LocalDate.parse(endDate, dateFormatter)

    var workDays = 0
    var current = start

    while (!current.isAfter(end)) {
        val dayOfWeek = current.dayOfWeek
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            workDays++
        }
        current = current.plusDays(1)
    }

    return workDays
}

actual fun getCurrentMonthStart(): String {
    val today = LocalDate.now()

    // Se hoje é dia 1 a 5, o mês de trabalho começou no dia 6 do mês anterior
    // Se hoje é dia 6 ou posterior, o mês de trabalho começou no dia 6 deste mês
    return if (today.dayOfMonth < 6) {
        today.minusMonths(1).withDayOfMonth(6).format(dateFormatter)
    } else {
        today.withDayOfMonth(6).format(dateFormatter)
    }
}

actual fun getCurrentMonthEnd(): String {
    val today = LocalDate.now()

    // Se hoje é dia 1 a 5, o mês termina no dia 5 deste mês
    // Se hoje é dia 6 ou posterior, o mês termina no dia 5 do próximo mês
    return if (today.dayOfMonth < 6) {
        today.withDayOfMonth(5).format(dateFormatter)
    } else {
        today.plusMonths(1).withDayOfMonth(5).format(dateFormatter)
    }
}

actual fun isClosingDay(): Boolean {
    return LocalDate.now().dayOfMonth == 5
}
