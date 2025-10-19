package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)
// Tailwind green-600 for closed/completed
private val TailwindGreen = Color(0xFF16A34A)

/**
 * Data class to represent a payroll period
 */
data class PayrollPeriod(
    val periodStartDate: String,
    val periodEndDate: String,
    val isCurrent: Boolean, // Is this the current active month?
    val payrolls: List<MonthlyPayroll>
)

/**
 * Screen for viewing monthly payroll history.
 * Shows list of payroll periods from newest to oldest.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(
    payrolls: List<MonthlyPayroll>,
    employees: List<Employee>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onPeriodClick: (PayrollPeriod) -> Unit = {}
) {
    // Group payrolls by period and sort by date (newest first)
    val payrollPeriods = remember(payrolls) {
        payrolls
            .groupBy { "${it.periodStartDate}|${it.periodEndDate}" }
            .map { (key, periodPayrolls) ->
                val parts = key.split("|")
                PayrollPeriod(
                    periodStartDate = parts[0],
                    periodEndDate = parts[1],
                    isCurrent = periodPayrolls.any { it.closedAt == null },
                    payrolls = periodPayrolls
                )
            }
            .sortedByDescending { it.periodStartDate } // Newest first
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Payroll") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Text("🔄")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TailwindBlue
                    )
                }
                payrollPeriods.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No payroll history yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payroll periods will appear here once generated.\nPeriods run from 6th to 5th of each month.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(payrollPeriods) { period ->
                            PayrollPeriodCard(
                                period = period,
                                employeeCount = employees.size,
                                onClick = { onPeriodClick(period) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PayrollPeriodCard(
    period: PayrollPeriod,
    employeeCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (period.isCurrent) {
                TailwindBlue.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Period dates
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📅",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formatPeriodLabel(period.periodStartDate, period.periodEndDate),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${period.periodStartDate} to ${period.periodEndDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status badge
                if (period.isCurrent) {
                    Badge(
                        containerColor = TailwindBlue,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "CURRENT",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Badge(
                        containerColor = TailwindGreen,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "CLOSED",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Summary info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Employee count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👷", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${period.payrolls.size} employees",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total payment
                val totalPayment = period.payrolls.sumOf {
                    it.totalPayment.toDoubleOrNull() ?: 0.0
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "R$ ${String.format("%.2f", totalPayment)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TailwindBlue
                    )
                }
            }

            // Current month hint
            if (period.isCurrent) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "→ Tap to view employee list",
                    style = MaterialTheme.typography.bodySmall,
                    color = TailwindBlue,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "→ Tap to view payroll details",
                    style = MaterialTheme.typography.bodySmall,
                    color = TailwindGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Format period label to show month/year
 * Example: "2025-01-06 to 2025-02-05" -> "January 2025"
 */
fun formatPeriodLabel(startDate: String, endDate: String): String {
    return try {
        val parts = startDate.split("-")
        val year = parts[0]
        val month = when (parts[1]) {
            "01" -> "January"
            "02" -> "February"
            "03" -> "March"
            "04" -> "April"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "August"
            "09" -> "September"
            "10" -> "October"
            "11" -> "November"
            "12" -> "December"
            else -> "Unknown"
        }
        "$month $year"
    } catch (e: Exception) {
        "Unknown Period"
    }
}
