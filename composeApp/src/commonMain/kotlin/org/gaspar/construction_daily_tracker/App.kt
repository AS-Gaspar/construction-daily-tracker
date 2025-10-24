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
                currentLanguage = viewModel.currentLanguage,
                strings = viewModel.strings,
                errorMessage = viewModel.errorMessage,
                successMessage = viewModel.successMessage,
                isLoading = viewModel.isLoading,
                showBackButton = false,
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { /* Can't go back from initial setup */ },
                onTestConnection = {
                    viewModel.testConnection()
                },
                onLanguageChange = { language ->
                    viewModel.changeLanguage(language)
                }
            )
        } else {
            // Main app navigation
            AppContent(viewModel)

            // Dialogs
            if (viewModel.showAddWorkDialog) {
                AddWorkDialog(
                    strings = viewModel.strings,
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
                    strings = viewModel.strings,
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
                strings = viewModel.strings,
                onNavigate = { viewModel.navigationState.navigateTo(it) }
            )
        }

        Screen.Works -> {
            WorksScreen(
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                onAddAdjustment = { employeeId ->
                    // Navigate to adjustment form
                    viewModel.navigationState.navigateTo(Screen.DayAdjustmentForm(employeeId))
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
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
                strings = viewModel.strings,
                payrolls = viewModel.payrolls,
                employees = viewModel.employees,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() },
                onPeriodClick = { period ->
                    // Navigate to works list for this payroll period
                    viewModel.navigationState.navigateTo(
                        Screen.PayrollWorksList(
                            periodStartDate = period.periodStartDate,
                            periodEndDate = period.periodEndDate
                        )
                    )
                }
            )
        }

        is Screen.ClosedPayrollDetail -> {
            ClosedPayrollDetailScreen(
                strings = viewModel.strings,
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

        is Screen.DayAdjustmentForm -> {
            val employee = viewModel.employees.find { it.id == screen.employeeId }
            DayAdjustmentFormScreen(
                employee = employee,
                strings = viewModel.strings,
                onBack = { viewModel.navigationState.navigateBack() },
                onSave = { date, adjustmentValue, notes ->
                    viewModel.createDayAdjustment(screen.employeeId, date, adjustmentValue, notes)
                },
                isLoading = viewModel.isLoading
            )
        }

        is Screen.PayrollWorksList -> {
            WorksPayrollListScreen(
                strings = viewModel.strings,
                periodStartDate = screen.periodStartDate,
                periodEndDate = screen.periodEndDate,
                payrolls = viewModel.payrolls,
                employees = viewModel.employees,
                works = viewModel.works,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() },
                onWorkClick = { workId ->
                    viewModel.navigationState.navigateTo(
                        Screen.PayrollWorkEmployees(
                            periodStartDate = screen.periodStartDate,
                            periodEndDate = screen.periodEndDate,
                            workId = workId
                        )
                    )
                }
            )
        }

        is Screen.PayrollWorkEmployees -> {
            WorkEmployeesPayrollScreen(
                strings = viewModel.strings,
                periodStartDate = screen.periodStartDate,
                periodEndDate = screen.periodEndDate,
                workId = screen.workId,
                payrolls = viewModel.payrolls,
                employees = viewModel.employees,
                works = viewModel.works,
                isLoading = viewModel.isLoading,
                onBack = { viewModel.navigationState.navigateBack() },
                onEmployeeClick = { employeeId ->
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employeeId))
                }
            )
        }

        Screen.Settings -> {
            SettingsScreen(
                currentServerUrl = viewModel.getCurrentServerUrl(),
                currentApiKey = viewModel.getCurrentApiKey(),
                currentLanguage = viewModel.currentLanguage,
                strings = viewModel.strings,
                errorMessage = viewModel.errorMessage,
                successMessage = viewModel.successMessage,
                isLoading = viewModel.isLoading,
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onTestConnection = {
                    viewModel.testConnection()
                },
                onLanguageChange = { language ->
                    viewModel.changeLanguage(language)
                }
            )
        }

        Screen.Configuration -> {
            ConfigurationScreen(
                strings = viewModel.strings,
                onNavigate = { viewModel.navigationState.navigateTo(it) },
                onBack = { viewModel.navigationState.navigateBack() }
            )
        }

        else -> {
            // Fallback to home for any unknown screens
            HomeScreen(
                strings = viewModel.strings,
                onNavigate = { viewModel.navigationState.navigateTo(it) }
            )
        }
    }
}
