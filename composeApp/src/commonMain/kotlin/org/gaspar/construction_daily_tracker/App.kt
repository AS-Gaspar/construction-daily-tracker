package org.gaspar.construction_daily_tracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.gaspar.construction_daily_tracker.navigation.Screen
import org.gaspar.construction_daily_tracker.ui.*
import org.gaspar.construction_daily_tracker.ui.screens.*

@Composable
fun App() {
    MaterialTheme {
        val viewModel: AppViewModel = viewModel { AppViewModel() }

        // Show settings screen if API is not configured
        if (!viewModel.isApiConfigured) {
            SettingsScreen(
                currentServerUrl = "",
                currentApiKey = "",
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { /* Can't go back from initial setup */ }
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

            if (viewModel.showGeneratePayrollDialog) {
                GeneratePayrollDialog(
                    onDismiss = { viewModel.showGeneratePayrollDialog = false },
                    onConfirm = { periodStartDate ->
                        viewModel.generatePayroll(periodStartDate)
                    }
                )
            }
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
                onWorkClick = { /* TODO: Edit work */ },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
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
                isLoading = viewModel.isLoading,
                onAddEmployee = {
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail())
                },
                onEmployeeClick = { employee ->
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        is Screen.EmployeeDetail -> {
            val employee = screen.employeeId?.let { id ->
                viewModel.employees.find { it.id == id }
            }
            EmployeeDetailScreen(
                employee = employee,
                works = viewModel.works,
                roles = viewModel.roles,
                isLoading = viewModel.isLoading,
                onSave = { name, surname, roleId, workId, dailyValue ->
                    viewModel.createEmployee(name, surname, roleId, workId, dailyValue)
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
                onGeneratePayroll = { viewModel.showGeneratePayrollDialog = true }
            )
        }

        Screen.Settings -> {
            SettingsScreen(
                currentServerUrl = "",
                currentApiKey = "",
                onSave = { serverUrl, apiKey ->
                    viewModel.configureApi(serverUrl, apiKey)
                },
                onBack = { viewModel.navigationState.navigateBack() }
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
