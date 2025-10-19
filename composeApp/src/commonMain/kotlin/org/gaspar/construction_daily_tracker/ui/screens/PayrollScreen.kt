package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.MonthlyPayroll

/**
 * Screen for viewing monthly payrolls.
 * Payroll period: 6th of one month to 5th of next month
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(
    payrolls: List<MonthlyPayroll>,
    employees: List<Employee>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onGeneratePayroll: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Payrolls") },
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
            FloatingActionButton(onClick = onGeneratePayroll) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("📊", style = MaterialTheme.typography.bodyLarge)
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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                payrolls.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No payrolls yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Generate a payroll to get started.\nPayroll periods run from the 6th to the 5th.",
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
                        // Group by period
                        val groupedPayrolls = payrolls.groupBy { "${it.periodStartDate} to ${it.periodEndDate}" }

                        groupedPayrolls.forEach { (period, periodPayrolls) ->
                            item {
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(periodPayrolls) { payroll ->
                                PayrollCard(
                                    payroll = payroll,
                                    employee = employees.find { it.id == payroll.employeeId }
                                )
                            }

                            item {
                                // Period summary
                                val totalPayment = periodPayrolls.sumOf {
                                    it.totalPayment.toDoubleOrNull() ?: 0.0
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Period Total:",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", totalPayment)}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PayrollCard(
    payroll: MonthlyPayroll,
    employee: Employee?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (payroll.closedAt != null) {
                        Text(
                            text = "✓ Closed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "○ Open",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "$${payroll.totalPayment}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            // Work details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PayrollDetailRow("Base workdays:", payroll.baseWorkdays)
                    PayrollDetailRow("Final worked days:", payroll.finalWorkedDays)
                }
                Column(modifier = Modifier.weight(1f)) {
                    employee?.let {
                        PayrollDetailRow("Daily rate:", "$${it.dailyValue}")
                    }
                }
            }
        }
    }
}

@Composable
fun PayrollDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Dialog for generating payroll for a specific period.
 */
@Composable
fun GeneratePayrollDialog(
    onDismiss: () -> Unit,
    onConfirm: (periodStartDate: String) -> Unit
) {
    var periodStartDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Payroll") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter the start date of the payroll period (6th of the month).",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = periodStartDate,
                    onValueChange = { periodStartDate = it },
                    label = { Text("Start Date (YYYY-MM-06)") },
                    placeholder = { Text("2025-10-06") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "The system will calculate payroll from the 6th to the 5th of the next month.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        periodStartDate.isBlank() -> errorMessage = "Start date is required"
                        !periodStartDate.matches(Regex("\\d{4}-\\d{2}-06")) ->
                            errorMessage = "Date must be the 6th (format: YYYY-MM-06)"
                        else -> {
                            onConfirm(periodStartDate)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
