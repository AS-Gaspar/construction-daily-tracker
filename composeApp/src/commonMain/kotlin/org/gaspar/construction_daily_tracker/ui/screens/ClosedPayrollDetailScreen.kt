package org.gaspar.construction_daily_tracker.ui.screens

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
import org.gaspar.construction_daily_tracker.model.Work
import org.gaspar.construction_daily_tracker.model.Role
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll
import org.gaspar.construction_daily_tracker.model.DayAdjustment

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)
// Tailwind green-600 for closed/completed
private val TailwindGreen = Color(0xFF16A34A)

/**
 * Data class for displaying employee payroll information
 */
data class EmployeePayrollDisplayData(
    val employee: Employee,
    val workName: String,
    val roleName: String,
    val payroll: MonthlyPayroll,
    val adjustmentSum: Double
)

/**
 * Screen for viewing closed payroll period details (read-only).
 * Shows all employees and their payroll information for a specific closed month.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClosedPayrollDetailScreen(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    periodStartDate: String,
    periodEndDate: String,
    payrolls: List<MonthlyPayroll>,
    employees: List<Employee>,
    works: List<Work>,
    roles: List<Role>,
    adjustments: List<DayAdjustment>,
    isLoading: Boolean,
    onBack: () -> Unit
) {
    // Filter payrolls for this specific period
    val periodPayrolls = remember(payrolls, periodStartDate, periodEndDate) {
        payrolls.filter {
            it.periodStartDate == periodStartDate && it.periodEndDate == periodEndDate
        }
    }

    // Build display data for each employee payroll
    val employeePayrollData = remember(periodPayrolls, employees, works, roles, adjustments) {
        periodPayrolls.mapNotNull { payroll ->
            val employee = employees.find { it.id == payroll.employeeId } ?: return@mapNotNull null
            val work = works.find { it.id == employee.workId }
            val role = roles.find { it.id == employee.roleId }

            // Calculate adjustment sum for this period
            val adjustmentSum = adjustments
                .filter { it.employeeId == employee.id }
                .filter { adjustment ->
                    adjustment.date >= periodStartDate && adjustment.date <= periodEndDate
                }
                .sumOf { it.adjustmentValue.toDoubleOrNull() ?: 0.0 }

            EmployeePayrollDisplayData(
                employee = employee,
                workName = work?.name ?: "Unknown",
                roleName = role?.title ?: "Unknown",
                payroll = payroll,
                adjustmentSum = adjustmentSum
            )
        }.sortedBy { it.employee.name.lowercase() }
    }

    // Calculate totals
    val totalPayment = remember(periodPayrolls) {
        periodPayrolls.sumOf { it.totalPayment.toDoubleOrNull() ?: 0.0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(formatPeriodLabel(strings, periodStartDate, periodEndDate)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                        color = TailwindGreen
                    )
                }
                employeePayrollData.isEmpty() -> {
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
                            text = strings.noPayrollDataFound,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Period summary card
                        item {
                            PeriodSummaryCard(
                                strings = strings,
                                periodStartDate = periodStartDate,
                                periodEndDate = periodEndDate,
                                employeeCount = employeePayrollData.size,
                                totalPayment = totalPayment
                            )
                        }

                        // Employee payroll cards
                        items(employeePayrollData) { data ->
                            EmployeePayrollCard(
                                strings = strings,
                                data = data
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodSummaryCard(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    periodStartDate: String,
    periodEndDate: String,
    employeeCount: Int,
    totalPayment: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TailwindGreen.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Column {
                    Text(
                        text = strings.closedPayrollPeriod,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$periodStartDate ${strings.periodRange.lowercase()} $periodEndDate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Badge(
                    containerColor = TailwindGreen,
                    contentColor = Color.White
                ) {
                    Text(
                        text = strings.statusClosed,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Employee count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👷", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$employeeCount ${if (employeeCount != 1) strings.employees else strings.employee}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total payment
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "R$ ${String.format("%.2f", totalPayment)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TailwindGreen
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeePayrollCard(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    data: EmployeePayrollDisplayData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Employee name and role
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👷",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${data.employee.name} ${data.employee.surname}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.roleName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Work site
            PayrollDetailRow(
                icon = "🏗️",
                label = strings.workSite,
                value = data.workName
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Daily value
            PayrollDetailRow(
                icon = "💵",
                label = strings.dailyValue,
                value = "R$ ${data.employee.dailyValue}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Base workdays
            PayrollDetailRow(
                icon = "📅",
                label = strings.baseWorkdays,
                value = "${data.payroll.baseWorkdays} ${strings.days}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Adjustments
            PayrollDetailRow(
                icon = "⚖️",
                label = strings.adjustments,
                value = String.format("%+.2f ${strings.days}", data.adjustmentSum),
                valueColor = if (data.adjustmentSum >= 0) TailwindGreen else Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Final worked days
            PayrollDetailRow(
                icon = "✅",
                label = strings.finalWorkedDays,
                value = "${data.payroll.finalWorkedDays} ${strings.days}",
                isBold = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Total payment (highlighted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.totalPayment,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "R$ ${data.payroll.totalPayment}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TailwindGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PayrollDetailRow(
    icon: String,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
