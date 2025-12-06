package org.gaspar.construction_daily_tracker

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.navigation.Screen
import org.gaspar.construction_daily_tracker.ui.*
import org.gaspar.construction_daily_tracker.ui.screens.*

@Composable
actual fun App() {
    val context = LocalContext.current
    val viewModel = remember {
        val database = AppDatabase.getInstance(context)
        LocalAppViewModel(database)
    }

    MaterialTheme {
        // Main app navigation (no settings screen needed for local-only app)
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
                strings = viewModel.strings,
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
    }
}

@Composable
private fun AppContent(viewModel: LocalAppViewModel) {
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
                    viewModel.navigationState.navigateTo(Screen.WorkDetail(work.id))
                },
                onBack = { viewModel.navigationState.navigateBack() },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        is Screen.WorkDetail -> {
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
                    viewModel.updateWork(updatedWork.id, updatedWork.name)
                },
                onDelete = { workToDelete ->
                    viewModel.deleteWork(workToDelete.id)
                },
                onEmployeeClick = { employee ->
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onAddEmployee = {
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
                    viewModel.navigationState.navigateTo(Screen.EmployeeEdit())
                },
                onEmployeeClick = { employee ->
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
                    viewModel.navigationState.navigateTo(Screen.EmployeeDetail(employee.id))
                },
                onRefresh = { viewModel.refreshAll() }
            )
        }

        is Screen.EmployeeDetail -> {
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
                    viewModel.navigationState.navigateTo(Screen.EmployeeEdit(emp.id))
                },
                onDelete = { emp ->
                    viewModel.deleteEmployee(emp.id)
                },
                onAddAdjustment = { employeeId ->
                    viewModel.navigationState.navigateTo(Screen.DayAdjustmentForm(employeeId))
                },
                onAssignToWork = { employeeId, workId ->
                    viewModel.assignEmployeeToWork(employeeId, workId)
                },
                onDeleteAdjustment = { adjustmentId ->
                    viewModel.deleteAdjustment(adjustmentId)
                },
                onClearSuccessMessage = { viewModel.clearSuccessMessage() }
            )
        }

        is Screen.EmployeeEdit -> {
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
                        viewModel.updateEmployee(employee.id, name, surname, roleId, workId, dailyValue)
                    } else {
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

        else -> {
            HomeScreen(
                strings = viewModel.strings,
                onNavigate = { viewModel.navigationState.navigateTo(it) }
            )
        }
    }
}
