package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.model.Employee
import org.gaspar.construction_daily_tracker.model.Role
import org.gaspar.construction_daily_tracker.model.Work

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)

/**
 * Screen for adding or editing an employee (FORM).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    strings: org.gaspar.construction_daily_tracker.i18n.Strings,
    employee: Employee? = null,
    works: List<Work>,
    roles: List<Role>,
    isLoading: Boolean,
    onSave: (name: String, surname: String, roleId: Int, workId: Int?, dailyValue: String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(employee?.name ?: "") }
    var surname by remember { mutableStateOf(employee?.surname ?: "") }
    var selectedWorkId by remember { mutableStateOf(employee?.workId) }
    var selectedRoleId by remember { mutableStateOf(employee?.roleId ?: roles.firstOrNull()?.id ?: 0) }
    var dailyValue by remember { mutableStateOf(employee?.dailyValue ?: "") }
    var showWorkDropdown by remember { mutableStateOf(false) }
    var showRoleDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (employee == null) "Add Employee" else "Edit Employee") },
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
            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Surname field
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Surname") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Work dropdown
            ExposedDropdownMenuBox(
                expanded = showWorkDropdown,
                onExpandedChange = { showWorkDropdown = !showWorkDropdown && !isLoading }
            ) {
                OutlinedTextField(
                    value = selectedWorkId?.let { id -> works.find { it.id == id }?.name } ?: "No Work",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Construction Name") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showWorkDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = showWorkDropdown,
                    onDismissRequest = { showWorkDropdown = false }
                ) {
                    // "No Work" option
                    DropdownMenuItem(
                        text = { Text("No Work") },
                        onClick = {
                            selectedWorkId = null
                            showWorkDropdown = false
                        }
                    )

                    // All available works
                    works.forEach { work ->
                        DropdownMenuItem(
                            text = { Text(work.name) },
                            onClick = {
                                selectedWorkId = work.id
                                showWorkDropdown = false
                            }
                        )
                    }
                }
            }

            // Role dropdown
            ExposedDropdownMenuBox(
                expanded = showRoleDropdown,
                onExpandedChange = { showRoleDropdown = !showRoleDropdown && !isLoading }
            ) {
                OutlinedTextField(
                    value = roles.find { it.id == selectedRoleId }?.title ?: "Select Role",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Job Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = showRoleDropdown,
                    onDismissRequest = { showRoleDropdown = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.title) },
                            onClick = {
                                selectedRoleId = role.id
                                showRoleDropdown = false
                            }
                        )
                    }
                }
            }

            // Daily value field
            OutlinedTextField(
                value = dailyValue,
                onValueChange = { dailyValue = it },
                label = { Text("Daily Rate ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                placeholder = { Text("0.00") }
            )

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "First name is required"
                        dailyValue.isBlank() -> errorMessage = "Daily rate is required"
                        dailyValue.toDoubleOrNull() == null -> errorMessage = "Invalid daily rate"
                        selectedRoleId == 0 -> errorMessage = "Please select a role"
                        else -> {
                            errorMessage = null
                            onSave(name.trim(), surname.trim(), selectedRoleId, selectedWorkId, dailyValue.trim())
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
                        color = Color.White
                    )
                } else {
                    Text(if (employee == null) "Add Employee" else "Save Changes")
                }
            }

            // Info card
            if (works.isEmpty() || roles.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚠️ Setup Required",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = strings.missingWorksAndRoles,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
