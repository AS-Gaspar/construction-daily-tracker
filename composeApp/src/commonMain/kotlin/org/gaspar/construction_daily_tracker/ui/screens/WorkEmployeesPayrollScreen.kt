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
import org.gaspar.construction_daily_tracker.i18n.Strings
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.Work
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)
// Tailwind green-600
private val TailwindGreen = Color(0xFF16A34A)

/**
 * Data class to hold employee payroll info
 */
data class EmployeePayrollInfo(
    val employee: Employee,
    val payroll: MonthlyPayroll
)

/**
 * Screen showing employees in a specific work for a payroll period
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkEmployeesPayrollScreen(
    strings: Strings,
    periodStartDate: String,
    periodEndDate: String,
    workId: Int,
    payrolls: List<MonthlyPayroll>,
    employees: List<Employee>,
    works: List<Work>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onEmployeeClick: (Int) -> Unit
) {
    val work = works.find { it.id == workId }

    // Filter employees in this work with payroll for this period
    val employeePayrollInfos = remember(payrolls, employees, workId, periodStartDate, periodEndDate) {
        val periodPayrolls = payrolls.filter {
            it.periodStartDate == periodStartDate && it.periodEndDate == periodEndDate
        }

        employees.filter { it.workId == workId }
            .mapNotNull { employee ->
                val payroll = periodPayrolls.find { it.employeeId == employee.id }
                if (payroll != null) {
                    EmployeePayrollInfo(employee, payroll)
                } else null
            }
            .sortedBy { it.employee.name.lowercase() }
    }

    // Calculate total for the work
    val totalPayment = employeePayrollInfos.sumOf {
        it.payroll.totalPayment.toDoubleOrNull() ?: 0.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(work?.name ?: strings.unknown)
                        Text(
                            text = formatPeriodLabel(strings, periodStartDate, periodEndDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindBlue,
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
                        color = TailwindBlue
                    )
                }
                employeePayrollInfos.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "👷",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = strings.noEmployeesInWorkPayroll,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary card
                        item {
                            WorkSummaryCard(
                                strings = strings,
                                workName = work?.name ?: strings.unknown,
                                employeeCount = employeePayrollInfos.size,
                                totalPayment = totalPayment
                            )
                        }

                        // Employee cards
                        items(employeePayrollInfos) { info ->
                            EmployeePayrollInfoCard(
                                strings = strings,
                                info = info,
                                onViewDetails = { onEmployeeClick(info.employee.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkSummaryCard(
    strings: Strings,
    workName: String,
    employeeCount: Int,
    totalPayment: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TailwindGreen.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Work name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏗️", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = workName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TailwindBlue
                )
            }

            HorizontalDivider()

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
fun EmployeePayrollInfoCard(
    strings: Strings,
    info: EmployeePayrollInfo,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Employee name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👤", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${info.employee.name} ${info.employee.surname}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // Daily value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.dailyValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "R$ ${info.employee.dailyValue}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Total to pay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.totalToPay,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "R$ ${info.payroll.totalPayment}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TailwindGreen
                )
            }

            // View details button
            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TailwindBlue
                )
            ) {
                Text(strings.viewEmployeeDetails)
            }
        }
    }
}
