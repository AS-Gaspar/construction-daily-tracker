package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.model.Work
import org.gaspar.construction_daily_tracker.model.Employee

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)
// Tailwind amber-500 for edit
private val TailwindAmber = Color(0xFFF59E0B)
// Tailwind red-600 for delete
private val TailwindRed = Color(0xFFDC2626)

/**
 * Screen showing detailed work information (VIEW ONLY).
 * Shows edit and delete buttons in the lower right corner.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkViewScreen(
    work: Work?,
    employees: List<Employee>,
    isLoading: Boolean,
    successMessage: String?,
    onBack: () -> Unit,
    onEdit: (Work) -> Unit,
    onDelete: (Work) -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onAddEmployee: () -> Unit,
    onClearSuccessMessage: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onClearSuccessMessage()
        }
    }

    // Filter employees for this work
    val workEmployees = remember(employees, work) {
        work?.let { w ->
            employees.filter { it.workId == w.id }
                .sortedBy { it.name.lowercase() }
        } ?: emptyList()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Work Details") },
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
            if (work != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit button (amber/yellow)
                    FloatingActionButton(
                        onClick = { showEditDialog = true },
                        containerColor = TailwindAmber,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Work"
                        )
                    }

                    // Delete button (red)
                    FloatingActionButton(
                        onClick = { showDeleteDialog = true },
                        containerColor = TailwindRed,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Work"
                        )
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
                work == null -> {
                    Text(
                        text = "Work not found",
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
                                    text = "🏗️",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = work.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TailwindBlue
                                )
                            }
                        }

                        // Work Details
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Work Information",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                // Work Name
                                DetailRow(
                                    label = "Work",
                                    value = work.name,
                                    icon = "🏗️"
                                )

                                // Number of employees
                                DetailRow(
                                    label = "Employees",
                                    value = "${workEmployees.size} employee${if (workEmployees.size != 1) "s" else ""}",
                                    icon = "👷"
                                )
                            }
                        }

                        // Employees assigned to this work
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Assigned Employees",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                if (workEmployees.isNotEmpty()) {
                                    workEmployees.forEach { employee ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = { onEmployeeClick(employee) },
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = MaterialTheme.shapes.medium
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "👤",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.padding(end = 12.dp)
                                                )
                                                Text(
                                                    text = "${employee.name} ${employee.surname}",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Add new employee button
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onAddEmployee),
                                    border = BorderStroke(2.dp, TailwindBlue),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = TailwindBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Add New Employee",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TailwindBlue
                                        )
                                    }
                                }
                            }
                        }

                        // Spacer for FAB
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Edit Dialog
    if (showEditDialog && work != null) {
        AddWorkDialog(
            work = work,
            onDismiss = { showEditDialog = false },
            onConfirm = { name ->
                onEdit(work.copy(name = name))
                showEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && work != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Work") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Are you sure you want to delete \"${work.name}\"?")
                    if (workEmployees.isNotEmpty()) {
                        Text(
                            text = "⚠️ Warning: This work has ${workEmployees.size} employee(s) assigned. Deleting it may affect their records.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(work)
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
