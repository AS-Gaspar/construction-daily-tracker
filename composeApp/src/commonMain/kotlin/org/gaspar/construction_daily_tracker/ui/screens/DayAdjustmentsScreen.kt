package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.model.DayAdjustment
import org.gaspar.construction_daily_tracker.model.Employee

/**
 * Screen for tracking daily work adjustments.
 * Examples: +1 for Saturday work, -0.5 for half-day absence
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayAdjustmentsScreen(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    adjustments: List<DayAdjustment>,
    employees: List<Employee>,
    isLoading: Boolean,
    onAddAdjustment: () -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.dailyWorkAdjustments) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Text("🔄")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAdjustment) {
                Text("➕", style = MaterialTheme.typography.headlineSmall)
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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                adjustments.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = strings.noAdjustmentsYet,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.adjustmentExamples,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(adjustments.sortedByDescending { it.date }) { adjustment ->
                            AdjustmentCard(
                                adjustment = adjustment,
                                employee = employees.find { it.id == adjustment.employeeId }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustmentCard(
    adjustment: DayAdjustment,
    employee: Employee?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (adjustment.adjustmentValue.toDoubleOrNull()?.let { it >= 0 } == true) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = employee?.let { "${it.name} ${it.surname}" } ?: "Unknown Employee",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = adjustment.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (adjustment.adjustmentValue.toDoubleOrNull()?.let { it >= 0 } == true) {
                        "+${adjustment.adjustmentValue}"
                    } else {
                        adjustment.adjustmentValue
                    } + " days",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (adjustment.adjustmentValue.toDoubleOrNull()?.let { it >= 0 } == true) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            adjustment.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Dialog for adding a new day adjustment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdjustmentDialog(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (employeeId: Int, date: String, adjustmentValue: String, notes: String) -> Unit
) {
    var selectedEmployeeId by remember { mutableStateOf(employees.firstOrNull()?.id ?: 0) }
    var date by remember { mutableStateOf("") }
    var adjustmentValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showEmployeeDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addWorkAdjustment) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Employee dropdown
                ExposedDropdownMenuBox(
                    expanded = showEmployeeDropdown,
                    onExpandedChange = { showEmployeeDropdown = !showEmployeeDropdown }
                ) {
                    OutlinedTextField(
                        value = employees.find { it.id == selectedEmployeeId }?.let { "${it.name} ${it.surname}" } ?: "Select Employee",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Employee") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEmployeeDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showEmployeeDropdown,
                        onDismissRequest = { showEmployeeDropdown = false }
                    ) {
                        employees.forEach { employee ->
                            DropdownMenuItem(
                                text = { Text("${employee.name} ${employee.surname}") },
                                onClick = {
                                    selectedEmployeeId = employee.id
                                    showEmployeeDropdown = false
                                }
                            )
                        }
                    }
                }

                // Date field
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    placeholder = { Text("2025-10-19") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Adjustment value
                OutlinedTextField(
                    value = adjustmentValue,
                    onValueChange = { adjustmentValue = it },
                    label = { Text("Adjustment (+/- days)") },
                    placeholder = { Text("+1 or -0.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("Saturday work, half-day absence, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        selectedEmployeeId == 0 -> errorMessage = "Please select an employee"
                        date.isBlank() -> errorMessage = "Date is required"
                        !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> errorMessage = "Invalid date format (use YYYY-MM-DD)"
                        adjustmentValue.isBlank() -> errorMessage = "Adjustment value is required"
                        adjustmentValue.toDoubleOrNull() == null -> errorMessage = "Invalid adjustment value"
                        else -> {
                            onConfirm(selectedEmployeeId, date, adjustmentValue, notes)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
