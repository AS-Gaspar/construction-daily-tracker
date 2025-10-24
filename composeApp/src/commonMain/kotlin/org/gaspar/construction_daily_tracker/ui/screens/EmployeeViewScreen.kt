package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
// Tailwind amber-500 for edit
private val TailwindAmber = Color(0xFFF59E0B)
// Tailwind red-600 for delete
private val TailwindRed = Color(0xFFDC2626)

/**
 * Screen showing detailed employee information (VIEW ONLY).
 * Shows edit and delete buttons in the lower right corner.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeViewScreen(
    employee: Employee?,
    works: List<Work>,
    roles: List<Role>,
    payrolls: List<MonthlyPayroll> = emptyList(),
    adjustments: List<DayAdjustment> = emptyList(),
    isLoading: Boolean,
    successMessage: String? = null,
    onBack: () -> Unit,
    onEdit: (Employee) -> Unit,
    onDelete: (Employee) -> Unit,
    onAddAdjustment: ((Int) -> Unit)? = null,
    onAssignToWork: ((Int, Int) -> Unit)? = null,
    onClearSuccessMessage: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAssignWorkDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onClearSuccessMessage?.invoke()
        }
    }

    // Calculate current total days
    val currentTotalDays = remember(employee, payrolls, adjustments) {
        employee?.let { emp ->
            val currentPayroll = payrolls
                .filter { it.employeeId == emp.id }
                .maxByOrNull { it.periodStartDate }

            if (currentPayroll != null) {
                val baseWorkdays = currentPayroll.baseWorkdays.toDoubleOrNull() ?: 0.0
                val adjustmentSum = adjustments
                    .filter { it.employeeId == emp.id }
                    .filter { adjustment ->
                        adjustment.date >= currentPayroll.periodStartDate &&
                        adjustment.date <= currentPayroll.periodEndDate
                    }
                    .sumOf { it.adjustmentValue.toDoubleOrNull() ?: 0.0 }

                String.format("%.2f", baseWorkdays + adjustmentSum)
            } else {
                "0.00"
            }
        } ?: "0.00"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Employee Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (employee != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete button (red)
                    FloatingActionButton(
                        onClick = { showDeleteDialog = true },
                        containerColor = TailwindRed,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Employee"
                        )
                    }

                    // Edit button (amber/yellow)
                    FloatingActionButton(
                        onClick = { onEdit(employee) },
                        containerColor = TailwindAmber,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Employee"
                        )
                    }

                    // Add Adjustment button (purple)
                    if (onAddAdjustment != null) {
                        FloatingActionButton(
                            onClick = { onAddAdjustment(employee.id) },
                            containerColor = Color(0xFF9333EA), // Tailwind purple-600
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "📊",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    // Assign to Work button (green) - only show if employee has no work
                    if (employee.workId == null && onAssignToWork != null) {
                        FloatingActionButton(
                            onClick = { showAssignWorkDialog = true },
                            containerColor = Color(0xFF16A34A), // Tailwind green-600
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "🏗️",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
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
                employee == null -> {
                    Text(
                        text = "Employee not found",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card with Icon and Name
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = TailwindBlue.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "👷",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "${employee.name}" + if (employee.surname.isNotEmpty()) " (${employee.surname})" else "",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TailwindBlue
                                )
                            }
                        }

                        // Employee Details
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Employee Information",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                // Name
                                DetailRow(
                                    label = "Name",
                                    value = employee.name,
                                    icon = "👤"
                                )

                                // Surname
                                DetailRow(
                                    label = "Surname",
                                    value = employee.surname,
                                    icon = "👤"
                                )

                                // Role/Profession
                                DetailRow(
                                    label = "Profession",
                                    value = roles.find { it.id == employee.roleId }?.title ?: "Unknown",
                                    icon = "🔧"
                                )

                                // Work
                                DetailRow(
                                    label = "Work Site",
                                    value = works.find { it.id == employee.workId }?.name ?: "Unknown",
                                    icon = "🏗️"
                                )

                                // Daily Value
                                DetailRow(
                                    label = "Daily Value",
                                    value = "R$ ${employee.dailyValue}",
                                    icon = "💵"
                                )

                                HorizontalDivider()

                                // Current Total Daily Wages
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "💰",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Current Total Days",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "This month's workdays",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$currentTotalDays days",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = TailwindBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "ℹ️ Information",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Current total days = Base workdays + Adjustments (overtime, absences, etc.)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Spacer for FAB
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && employee != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Employee") },
            text = { Text("Are you sure you want to delete ${employee.name} ${employee.surname}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(employee)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TailwindRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Assign to Work Dialog
    if (showAssignWorkDialog && employee != null && onAssignToWork != null) {
        var selectedWorkId by remember { mutableStateOf(0) }

        AlertDialog(
            onDismissRequest = { showAssignWorkDialog = false },
            title = { Text("Assign to Work") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select a work site to assign ${employee.name} ${employee.surname} to:")

                    // Dropdown for selecting work
                    var expanded by remember { mutableStateOf(false) }
                    val selectedWork = works.find { it.id == selectedWorkId }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedWork?.name ?: "Select work...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Work Site") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            works.forEach { work ->
                                DropdownMenuItem(
                                    text = { Text(work.name) },
                                    onClick = {
                                        selectedWorkId = work.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedWorkId != 0) {
                            onAssignToWork(employee.id, selectedWorkId)
                            showAssignWorkDialog = false
                        }
                    },
                    enabled = selectedWorkId != 0
                ) {
                    Text("Assign")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignWorkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
