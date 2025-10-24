package org.gaspar.construction_daily_tracker.utils

import org.junit.Test
import kotlin.test.assertEquals

class WorkDaysCalculator2025Test {

    @Test
    fun `should calculate 23 workdays for October 6 2025 to November 5 2025`() {
        // Given: Period from October 6, 2025 to November 5, 2025
        val startDate = "2025-10-06"
        val endDate = "2025-11-05"

        // When: Calculate work days
        val workDays = calculateWorkDays(startDate, endDate)

        // Then: Should have 23 workdays (Monday-Friday only)
        // Let's count manually:
        // Oct 2025: 6(Mon), 7(Tue), 8(Wed), 9(Thu), 10(Fri) = 5 days
        //           13(Mon), 14(Tue), 15(Wed), 16(Thu), 17(Fri) = 5 days
        //           20(Mon), 21(Tue), 22(Wed), 23(Thu), 24(Fri) = 5 days
        //           27(Mon), 28(Tue), 29(Wed), 30(Thu), 31(Fri) = 5 days
        // Nov 2025: 3(Mon), 4(Tue), 5(Wed) = 3 days
        // Total: 5 + 5 + 5 + 5 + 3 = 23 workdays
        assertEquals(23, workDays, "October 6, 2025 to November 5, 2025 should have 23 workdays")
    }

    @Test
    fun `should print detailed calendar for October 6 2025 to November 5 2025`() {
        val startDate = "2025-10-06"
        val endDate = "2025-11-05"

        println("\n========================================")
        println("Workday Calculation: $startDate to $endDate")
        println("========================================\n")

        val start = java.time.LocalDate.parse(startDate)
        val end = java.time.LocalDate.parse(endDate)

        var current = start
        var workDayCount = 0

        while (!current.isAfter(end)) {
            val dayOfWeek = current.dayOfWeek
            val isWorkday = dayOfWeek != java.time.DayOfWeek.SATURDAY &&
                           dayOfWeek != java.time.DayOfWeek.SUNDAY

            if (isWorkday) {
                workDayCount++
                println("$current ($dayOfWeek) - WORKDAY #$workDayCount")
            } else {
                println("$current ($dayOfWeek) - WEEKEND")
            }

            current = current.plusDays(1)
        }

        println("\n========================================")
        println("TOTAL WORKDAYS: $workDayCount")
        println("========================================\n")

        assertEquals(23, workDayCount)
    }
}
