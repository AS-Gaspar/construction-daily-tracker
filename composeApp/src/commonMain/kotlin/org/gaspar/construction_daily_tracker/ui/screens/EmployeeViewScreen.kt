package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
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
    onDeleteAdjustment: ((Int) -> Unit)? = null,
    onClearSuccessMessage: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAssignWorkDialog by remember { mutableStateOf(false) }
    var showActionsMenu by remember { mutableStateOf(false) }
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
                title = { Text(strings.employeeDetails) },
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
                employee == null -> {
                    Text(
                        text = strings.employeeNotFound,
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
                            Box(
                                modifier = Modifier.fillMaxWidth()
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

                                // Burger Menu Icon
                                IconButton(
                                    onClick = { showActionsMenu = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Actions",
                                        tint = TailwindBlue,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
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
                                    text = strings.employeeInformation,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                // Name
                                DetailRow(
                                    label = strings.name,
                                    value = employee.name,
                                    icon = "👤"
                                )

                                // Surname
                                DetailRow(
                                    label = strings.surname,
                                    value = employee.surname,
                                    icon = "👤"
                                )

                                // Role/Profession
                                DetailRow(
                                    label = strings.profession,
                                    value = roles.find { it.id == employee.roleId }?.title ?: strings.unknown,
                                    icon = "🔧"
                                )

                                // Work
                                DetailRow(
                                    label = strings.workSite,
                                    value = works.find { it.id == employee.workId }?.name ?: strings.unknown,
                                    icon = "🏗️"
                                )

                                // Daily Value
                                DetailRow(
                                    label = strings.dailyValue,
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
                                                text = strings.currentTotalDays,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = strings.thisMonthWorkdays,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$currentTotalDays ${strings.days}",
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
                                    text = "ℹ️ ${strings.configurationInfo}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = strings.currentTotalDaysFormula,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Adjustments History Table
                        AdjustmentsHistoryTable(
                            strings = strings,
                            employeeId = employee.id,
                            adjustments = adjustments,
                            onDeleteAdjustment = onDeleteAdjustment
                        )
                    }
                }
            }
        }
    }

    // Actions Menu Dialog
    if (showActionsMenu && employee != null) {
        AlertDialog(
            onDismissRequest = { showActionsMenu = false },
            title = {
                Text(
                    text = strings.employeeDetails,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Add Adjustment Button
                    if (onAddAdjustment != null) {
                        Button(
                            onClick = {
                                showActionsMenu = false
                                onAddAdjustment(employee.id)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF16A34A) // Tailwind green-600
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✚", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = strings.addAdjustment,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Edit Button
                    Button(
                        onClick = {
                            showActionsMenu = false
                            onEdit(employee)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TailwindAmber
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.edit,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = strings.editEmployee,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Delete Button
                    Button(
                        onClick = {
                            showActionsMenu = false
                            showDeleteDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TailwindRed
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = strings.delete,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = strings.deleteEmployee,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Assign to Work Button - only show if employee has no work
                    if (employee.workId == null && onAssignToWork != null) {
                        Button(
                            onClick = {
                                showActionsMenu = false
                                showAssignWorkDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TailwindBlue
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🏗️", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = strings.assignToWork,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionsMenu = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && employee != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(strings.deleteEmployee) },
            text = { Text("${strings.deleteEmployeeQuestion} ${employee.name} ${employee.surname}? ${strings.actionCannotBeUndone}") },
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
                    Text(strings.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Assign to Work Dialog
    if (showAssignWorkDialog && employee != null && onAssignToWork != null) {
        var selectedWorkId by remember { mutableStateOf(0) }

        AlertDialog(
            onDismissRequest = { showAssignWorkDialog = false },
            title = { Text(strings.assignWorkDialogTitle) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("${strings.selectWork}: ${employee.name} ${employee.surname}")

                    // Dropdown for selecting work
                    var expanded by remember { mutableStateOf(false) }
                    val selectedWork = works.find { it.id == selectedWorkId }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedWork?.name ?: strings.selectWork,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(strings.workSite) },
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

@Composable
private fun AdjustmentsHistoryTable(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    employeeId: Int,
    adjustments: List<DayAdjustment>,
    onDeleteAdjustment: ((Int) -> Unit)?
) {
    var selectedNote by remember { mutableStateOf<String?>(null) }
    var adjustmentToDelete by remember { mutableStateOf<DayAdjustment?>(null) }

    // Filter adjustments for this employee and sort by date (most recent first)
    val employeeAdjustments = remember(adjustments, employeeId) {
        adjustments
            .filter { it.employeeId == employeeId }
            .sortedByDescending { it.date }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.adjustmentsHistory,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            if (employeeAdjustments.isEmpty()) {
                Text(
                    text = strings.noAdjustments,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                // Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.date,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = strings.value,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = strings.note,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (onDeleteAdjustment != null) {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                HorizontalDivider()

                // Table Rows
                employeeAdjustments.forEach { adjustment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date
                        Text(
                            text = adjustment.date,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Value
                        Text(
                            text = adjustment.adjustmentValue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (adjustment.adjustmentValue.startsWith("-"))
                                TailwindRed
                            else
                                Color(0xFF16A34A), // Tailwind green
                            modifier = Modifier.weight(0.8f)
                        )

                        // Note Button
                        Button(
                            onClick = {
                                selectedNote = adjustment.notes ?: strings.noNoteAvailable
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TailwindBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = strings.viewNote,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Delete Button
                        if (onDeleteAdjustment != null) {
                            IconButton(
                                onClick = { adjustmentToDelete = adjustment },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = strings.deleteAdjustment,
                                    tint = TailwindRed
                                )
                            }
                        }
                    }

                    if (adjustment != employeeAdjustments.last()) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    // Note Dialog
    if (selectedNote != null) {
        AlertDialog(
            onDismissRequest = { selectedNote = null },
            title = { Text(strings.noteDialogTitle) },
            text = {
                Text(
                    text = selectedNote ?: strings.noNoteAvailable,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedNote = null }) {
                    Text(strings.ok)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (adjustmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { adjustmentToDelete = null },
            title = { Text(strings.deleteAdjustment) },
            text = {
                Text(
                    text = strings.deleteAdjustmentConfirm,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAdjustment?.invoke(adjustmentToDelete!!.id)
                        adjustmentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TailwindRed
                    )
                ) {
                    Text(strings.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { adjustmentToDelete = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
