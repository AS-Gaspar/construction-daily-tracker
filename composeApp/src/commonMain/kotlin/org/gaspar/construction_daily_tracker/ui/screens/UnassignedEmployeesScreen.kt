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
import org.gaspar.construction_daily_tracker.model.Role
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll
import org.gaspar.construction_daily_tracker.model.DayAdjustment

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)

/**
 * Screen displaying employees without a work assignment (workId = null).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnassignedEmployeesScreen(
    strings: Strings,
    employees: List<Employee>,
    roles: List<Role>,
    payrolls: List<MonthlyPayroll> = emptyList(),
    adjustments: List<DayAdjustment> = emptyList(),
    isLoading: Boolean,
    onBack: () -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onRefresh: () -> Unit
) {
    // Filter employees with workId = null (unassigned)
    val unassignedEmployees = remember(employees) {
        employees.filter { it.workId == null }
            .sortedBy { it.name.lowercase() }
    }

    // Calculate display data for each employee
    val employeeDisplayData = remember(unassignedEmployees, roles, payrolls, adjustments, strings) {
        unassignedEmployees.map { employee ->
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
                workName = strings.noWork ?: "No Work",
                roleName = role?.title ?: strings.unknown,
                currentTotalDays = totalDays
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.unassignedEmployees)
                        Text(
                            text = "${unassignedEmployees.size} ${if (unassignedEmployees.size != 1) strings.employees else strings.employee}",
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
                unassignedEmployees.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "✅",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = strings.allEmployeesAssigned,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.noEmployeesUnassigned,
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
                        // Header card with info
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚠️",
                                        style = MaterialTheme.typography.displaySmall,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = strings.employeesWithoutWork,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = strings.clickToAssignWork,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }

                        // List all unassigned employees
                        items(employeeDisplayData) { displayData ->
                            EmployeeCard(
                                strings = strings,
                                displayData = displayData,
                                onClick = { onEmployeeClick(displayData.employee) }
                            )
                        }
                    }
                }
            }
        }
    }
}
