package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import org.gaspar.construction_daily_tracker.model.Role
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll
import org.gaspar.construction_daily_tracker.model.DayAdjustment

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)

/**
 * Data class to hold employee display information including calculated wages
 */
data class EmployeeDisplayData(
    val employee: Employee,
    val workName: String,
    val roleName: String,
    val currentTotalDays: String // Calculated: baseWorkdays + adjustments
)

/**
 * Screen displaying list of employees in alphabetical order.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    strings: Strings,
    employees: List<Employee>,
    works: List<Work>,
    roles: List<Role>,
    payrolls: List<MonthlyPayroll> = emptyList(),
    adjustments: List<DayAdjustment> = emptyList(),
    isLoading: Boolean,
    onAddEmployee: () -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    // Sort employees alphabetically by name
    val sortedEmployees = remember(employees) {
        employees.sortedBy { it.name.lowercase() }
    }

    // Calculate display data for each employee
    val employeeDisplayData = remember(sortedEmployees, works, roles, payrolls, adjustments, strings) {
        sortedEmployees.map { employee ->
            val work = works.find { it.id == employee.workId }
            val role = roles.find { it.id == employee.roleId }

            // Get current month payroll for this employee
            val currentPayroll = payrolls
                .filter { it.employeeId == employee.id }
                .maxByOrNull { it.periodStartDate }

            // Calculate total days: base workdays + adjustments
            val totalDays = if (currentPayroll != null) {
                val baseWorkdays = currentPayroll.baseWorkdays.toDoubleOrNull() ?: 0.0

                // Sum all adjustments for this employee in current period
                val adjustmentSum = adjustments
                    .filter { it.employeeId == employee.id }
                    .filter { adjustment ->
                        // Check if adjustment is within payroll period
                        adjustment.date >= currentPayroll.periodStartDate &&
                        adjustment.date <= currentPayroll.periodEndDate
                    }
                    .sumOf { it.adjustmentValue.toDoubleOrNull() ?: 0.0 }

                String.format("%.2f", baseWorkdays + adjustmentSum)
            } else {
                "0.00"
            }

            EmployeeDisplayData(
                employee = employee,
                workName = work?.name ?: strings.unknown,
                roleName = role?.title ?: strings.unknown,
                currentTotalDays = totalDays
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.employeesTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
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
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // List all employees alphabetically
                        items(employeeDisplayData) { displayData ->
                            EmployeeCard(
                                strings = strings,
                                displayData = displayData,
                                onClick = { onEmployeeClick(displayData.employee) }
                            )
                        }

                        // Add new employee button at the bottom
                        item {
                            AddNewEmployeeButton(
                                strings = strings,
                                onClick = onAddEmployee
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(
    strings: Strings,
    displayData: EmployeeDisplayData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = "👷",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Employee info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name
                Text(
                    text = "${displayData.employee.name}" + if (displayData.employee.surname.isNotEmpty()) " (${displayData.employee.surname})" else "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Work
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏗️",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayData.workName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))

                // Current Total Days (wages calculation)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${displayData.currentTotalDays} ${strings.days}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TailwindBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewEmployeeButton(
    strings: Strings,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = BorderStroke(2.dp, TailwindBlue),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = strings.add,
                tint = TailwindBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = strings.addNewEmployee,
                style = MaterialTheme.typography.titleLarge,
                color = TailwindBlue
            )
        }
    }
}
