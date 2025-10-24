package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.i18n.Strings
import org.gaspar.construction_daily_tracker.model.Employee
import java.text.SimpleDateFormat
import java.util.*

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)

/**
 * Screen for adding a daily adjustment value for an employee.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayAdjustmentFormScreen(
    employee: Employee?,
    strings: Strings,
    onBack: () -> Unit,
    onSave: (date: String, adjustmentValue: String, notes: String) -> Unit,
    isLoading: Boolean = false
) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedAdjustmentValue by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAdjustmentDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val adjustmentOptions = listOf("-1.0", "-0.5", "0.5", "1.0")

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.addAdjustmentValue) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Employee info card
            employee?.let { emp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = TailwindBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = strings.employeeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${emp.name} ${emp.surname}",
                            style = MaterialTheme.typography.titleLarge,
                            color = TailwindBlue
                        )
                    }
                }
            }

            // Date picker field
            OutlinedTextField(
                value = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("${strings.adjustmentDate} *") },
                placeholder = { Text(strings.selectDate) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("📅")
                    }
                }
            )

            // Adjustment value dropdown
            ExposedDropdownMenuBox(
                expanded = showAdjustmentDropdown,
                onExpandedChange = { showAdjustmentDropdown = !showAdjustmentDropdown }
            ) {
                OutlinedTextField(
                    value = selectedAdjustmentValue?.let {
                        val value = it.toDoubleOrNull() ?: 0.0
                        if (value >= 0) "+$it days" else "$it days"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("${strings.adjustmentValue} *") },
                    placeholder = { Text(strings.selectValue) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAdjustmentDropdown) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showAdjustmentDropdown,
                    onDismissRequest = { showAdjustmentDropdown = false }
                ) {
                    adjustmentOptions.forEach { value ->
                        DropdownMenuItem(
                            text = {
                                val description = when (value) {
                                    "1.0" -> strings.fullDayWork
                                    "0.5" -> strings.halfDayWork
                                    "-0.5" -> strings.halfDayAbsence
                                    "-1.0" -> strings.fullDayAbsence
                                    else -> value
                                }
                                Text(description)
                            },
                            onClick = {
                                selectedAdjustmentValue = value
                                showAdjustmentDropdown = false
                            }
                        )
                    }
                }
            }

            // Notes field (optional)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(strings.notesOptional) },
                placeholder = { Text(strings.notesPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    when {
                        selectedDate == null -> errorMessage = strings.pleaseSelectDate
                        selectedAdjustmentValue == null -> errorMessage = strings.pleaseSelectValue
                        else -> {
                            val formattedDate = dateFormatter.format(Date(selectedDate!!))
                            onSave(formattedDate, selectedAdjustmentValue!!, notes.trim())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TailwindBlue,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) strings.saving else strings.saveAdjustment)
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = datePickerState.selectedDateMillis
                            showDatePicker = false
                            errorMessage = null
                        }
                    ) {
                        Text(strings.ok)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(strings.cancel)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
