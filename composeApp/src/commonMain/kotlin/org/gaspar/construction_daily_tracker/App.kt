package org.gaspar.construction_daily_tracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.gaspar.construction_daily_tracker.data.rememberCredentialStorage
import org.gaspar.construction_daily_tracker.navigation.Screen
import org.gaspar.construction_daily_tracker.ui.*
import org.gaspar.construction_daily_tracker.ui.screens.*

@Composable
fun App() {
    MaterialTheme {
        val credentialStorage = rememberCredentialStorage()
        val viewModel: AppViewModel = viewModel { AppViewModel(credentialStorage) }

        // Show settings screen if API is not configured
        if (!viewModel.isApiConfigured) {
            SettingsScreen(
                currentServerUrl = viewModel.getCurrentServerUrl(),
                currentApiKey = viewModel.getCurrentApiKey(),
                errorMessage = viewModel.errorMessage,
                isLoading = viewModel.isLoading,
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { /* Can't go back from initial setup */ },
                onTestConnection = {
                    viewModel.testConnection()
                }
            )
        } else {
            // Main app navigation
            AppContent(viewModel)

            // Dialogs
            if (viewModel.showAddWorkDialog) {
                AddWorkDialog(
                    onDismiss = { viewModel.showAddWorkDialog = false },
                    onConfirm = { name -> viewModel.createWork(name) }
                )
            }

            if (viewModel.showAddRoleDialog) {
                AddRoleDialog(
                    onDismiss = { viewModel.showAddRoleDialog = false },
                    onConfirm = { title -> viewModel.createRole(title) }
                )
            }

            if (viewModel.showAddAdjustmentDialog) {
                AddAdjustmentDialog(
                    employees = viewModel.employees,
                    onDismiss = { viewModel.showAddAdjustmentDialog = false },
                    onConfirm = { employeeId, date, adjustmentValue, notes ->
                        viewModel.createAdjustment(employeeId, date, adjustmentValue, notes)
                    }
                )
            }

            // TODO: Implement GeneratePayrollDialog if needed
            // if (viewModel.showGeneratePayrollDialog) {
            //     GeneratePayrollDialog(...)
            // }
        }
    }
}

@Composable
fun AppContent(viewModel: AppViewModel) {
    when (val screen = viewModel.navigationState.currentScreen) {
        Screen.Home -> {
            HomeScreen(
                onNavigate = { viewModel.navigationState.navigateTo(it) }
            )
        }

        Screen.Works -> {
            WorksScreen(
                works = viewModel.works,
                isLoading = viewModel.isLoading,
                onAddWork = { viewModel.showAddWorkDialog = true },
                onWorkClick = { work ->
                    // Navigate to work detail showing all employees for this work
                    viewModel.navigationState.navigateTo(Screen.WorkDetail(work.id))
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        is Screen.WorkDetail -> {
            // Work detail screen - shows work info with edit/delete buttons
            val work = screen.workId?.let { id ->
                viewModel.works.find { it.id == id }
            }
            WorkViewScreen(
                work = work,
                employees = viewModel.employees,
                isLoading = viewModel.isLoading,
                successMessage = viewModel.successMessage,
                onBack = { viewModel.navigationState.navigateBack() },
                onEdit = { updatedWork ->
                    // Update work with new name
                    viewModel.updateWork(updatedWork.id, updatedWork.name)
                },
                onDelete = { workToDelete ->
                    // Delete work
                    viewModel.deleteWork(workToDelete.id)
                },
                onEmployeeClick = { employee ->
                    // Navigate to employee view screen
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onAddEmployee = {
                    // Navigate to add new employee form
                    viewModel.navigationState.navigateTo(Screen.EmployeeEdit())
                },
                onClearSuccessMessage = { viewModel.clearSuccessMessage() }
            )
        }

        Screen.Roles -> {
            RolesScreen(
                roles = viewModel.roles,
                isLoading = viewModel.isLoading,
                onAddRole = { viewModel.showAddRoleDialog = true },
                onRoleClick = { /* TODO: Edit role */ },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        Screen.Employees -> {
            EmployeeListScreen(
                employees = viewModel.employees,
                works = viewModel.works,
                roles = viewModel.roles,
                payrolls = viewModel.payrolls,
                adjustments = viewModel.dayAdjustments,
                isLoading = viewModel.isLoading,
                onAddEmployee = {
                    // Navigate to form screen for adding new employee
                    viewModel.navigationState.navigateTo(Screen.EmployeeEdit())
                },
                onEmployeeClick = { employee ->
                    // Navigate to view screen to see employee details
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        Screen.UnassignedEmployees -> {
            UnassignedEmployeesScreen(
                employees = viewModel.employees,
                roles = viewModel.roles,
                payrolls = viewModel.payrolls,
                adjustments = viewModel.dayAdjustments,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() },
                onEmployeeClick = { employee ->
                    // Navigate to employee view screen to assign work
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        is Screen.EmployeeDetail -> {
            // View screen - shows employee details with edit/delete buttons
            val employee = screen.employeeId?.let { id ->
                viewModel.employees.find { it.id == id }
            }
            EmployeeViewScreen(
                employee = employee,
                works = viewModel.works,
                roles = viewModel.roles,
                payrolls = viewModel.payrolls,
                adjustments = viewModel.dayAdjustments,
                isLoading = viewModel.isLoading,
                successMessage = viewModel.successMessage,
                onBack = { viewModel.navigationState.navigateBack() },
                onEdit = { emp ->
                    // Navigate to edit screen
                    viewModel.navigationState.navigateTo(Screen.EmployeeEdit(emp.id))
                },
                onDelete = { emp ->
                    // Delete employee
                    viewModel.deleteEmployee(emp.id)
                },
                onAssignToWork = { employeeId, workId ->
                    // Assign employee to work
                    viewModel.assignEmployeeToWork(employeeId, workId)
                },
                onClearSuccessMessage = { viewModel.clearSuccessMessage() }
            )
        }

        is Screen.EmployeeEdit -> {
            // Form screen - for adding or editing employee
            val employee = screen.employeeId?.let { id ->
                viewModel.employees.find { it.id == id }
            }
            EmployeeFormScreen(
                employee = employee,
                works = viewModel.works,
                roles = viewModel.roles,
                isLoading = viewModel.isLoading,
                onSave = { name, surname, roleId, workId, dailyValue ->
                    if (employee != null) {
                        // Update existing employee
                        viewModel.updateEmployee(employee.id, name, surname, roleId, workId, dailyValue)
                    } else {
                        // Create new employee
                        viewModel.createEmployee(name, surname, roleId, workId, dailyValue)
                    }
                },
                onBack = { viewModel.navigationState.navigateBack() }
            )
        }

        Screen.DailyAdjustments -> {
            DayAdjustmentsScreen(
                adjustments = viewModel.dayAdjustments,
                employees = viewModel.employees,
                isLoading = viewModel.isLoading,
                onAddAdjustment = { viewModel.showAddAdjustmentDialog = true },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        Screen.Payroll -> {
            PayrollScreen(
                payrolls = viewModel.payrolls,
                employees = viewModel.employees,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() },
                onPeriodClick = { period ->
                    if (period.isCurrent) {
                        // Navigate to Employees for current month
                        viewModel.navigationState.navigateTo(Screen.Employees)
                    } else {
                        // Navigate to closed payroll detail
                        viewModel.navigationState.navigateTo(
                            Screen.ClosedPayrollDetail(
                                periodStartDate = period.periodStartDate,
                                periodEndDate = period.periodEndDate
                            )
                        )
                    }
                }
            )
        }

        is Screen.ClosedPayrollDetail -> {
            ClosedPayrollDetailScreen(
                periodStartDate = screen.periodStartDate,
                periodEndDate = screen.periodEndDate,
                payrolls = viewModel.payrolls,
                employees = viewModel.employees,
                works = viewModel.works,
                roles = viewModel.roles,
                adjustments = viewModel.dayAdjustments,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() }
            )
        }

        Screen.Settings -> {
            SettingsScreen(
                currentServerUrl = viewModel.getCurrentServerUrl(),
                currentApiKey = viewModel.getCurrentApiKey(),
                errorMessage = viewModel.errorMessage,
                isLoading = viewModel.isLoading,
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onTestConnection = {
                    viewModel.testConnection()
                }
            )
        }

        Screen.Configuration -> {
            ConfigurationScreen(
                onNavigate = { viewModel.navigationState.navigateTo(it) },
                onBack = { viewModel.navigationState.navigateBack() }
            )
        }

        else -> {
            // Fallback to home for any unknown screens
            HomeScreen(
                onNavigate = { viewModel.navigationState.navigateTo(it) }
            )
        }
    }
}
