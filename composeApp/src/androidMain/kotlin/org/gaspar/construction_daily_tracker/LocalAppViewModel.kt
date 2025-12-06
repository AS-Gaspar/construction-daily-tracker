package org.gaspar.construction_daily_tracker

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.gaspar.construction_daily_tracker.database.AppDatabase
import org.gaspar.construction_daily_tracker.i18n.Strings
import org.gaspar.construction_daily_tracker.i18n.PortugueseStrings
import org.gaspar.construction_daily_tracker.model.*
import org.gaspar.construction_daily_tracker.navigation.NavigationState
import org.gaspar.construction_daily_tracker.repository.*

/**
 * Main ViewModel for the app, managing all data and navigation using local database.
 */
class LocalAppViewModel(
    private val database: AppDatabase
) : ViewModel() {

    // Navigation
    val navigationState = NavigationState()

    // Local Repositories
    private val workRepository = LocalWorkRepository(database)
    private val roleRepository = LocalRoleRepository(database)
    private val employeeRepository = LocalEmployeeRepository(database)
    private val dayAdjustmentRepository = LocalDayAdjustmentRepository(database)
    private val payrollRepository = LocalPayrollRepository(database)

    // State
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // Strings always in Portuguese
    val strings: Strings = PortugueseStrings()

    init {
        // Load initial data
        refreshAll()
    }

    // Data
    var works by mutableStateOf<List<Work>>(emptyList())
        private set

    var roles by mutableStateOf<List<Role>>(emptyList())
        private set

    var employees by mutableStateOf<List<Employee>>(emptyList())
        private set

    var dayAdjustments by mutableStateOf<List<DayAdjustment>>(emptyList())
        private set

    var payrolls by mutableStateOf<List<MonthlyPayroll>>(emptyList())
        private set

    // Dialog states
    var showAddWorkDialog by mutableStateOf(false)
    var showAddRoleDialog by mutableStateOf(false)
    var showAddAdjustmentDialog by mutableStateOf(false)
    var showGeneratePayrollDialog by mutableStateOf(false)

    /**
     * Refresh all data from the local database.
     */
    fun refreshAll() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                println("🔄 REFRESH ALL DATA START")
                works = workRepository.getAllWorks()
                println("   ✓ Loaded ${works.size} works")
                roles = roleRepository.getAllRoles()
                println("   ✓ Loaded ${roles.size} roles")
                employees = employeeRepository.getAllEmployees()
                println("   ✓ Loaded ${employees.size} employees")
                dayAdjustments = dayAdjustmentRepository.getAllAdjustments()
                println("   ✓ Loaded ${dayAdjustments.size} day adjustments")
                payrolls = payrollRepository.getAllPayrolls()
                println("   ✓ Loaded ${payrolls.size} payrolls")
                println("✅ REFRESH ALL DATA SUCCESS\n")
            } catch (e: Exception) {
                println("❌ REFRESH ALL DATA FAILED: ${e.message}")
                println(e.stackTraceToString())
                errorMessage = "Failed to load data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearSuccessMessage() {
        successMessage = null
    }

    // Work operations
    fun createWork(name: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                workRepository.createWork(name)
                works = workRepository.getAllWorks()
                showAddWorkDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create work: ${e.message}"
                println("Error in createWork: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    fun updateWork(id: Int, name: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                workRepository.updateWork(id, name)
                works = workRepository.getAllWorks()
                successMessage = "Work updated successfully!"
            } catch (e: Exception) {
                errorMessage = "Failed to update work: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteWork(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                workRepository.deleteWork(id)
                works = workRepository.getAllWorks()
                employees = employeeRepository.getAllEmployees()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to delete work: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun assignEmployeeToWork(employeeId: Int, workId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                employeeRepository.assignToWork(employeeId, workId)
                employees = employeeRepository.getAllEmployees()
                successMessage = "Employee assigned successfully!"
            } catch (e: Exception) {
                errorMessage = "Failed to assign employee: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Role operations
    fun createRole(title: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                roleRepository.createRole(title)
                roles = roleRepository.getAllRoles()
                showAddRoleDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create role: ${e.message}"
                println("Error in createRole: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    // Employee operations
    fun createEmployee(
        name: String,
        surname: String,
        roleId: Int,
        workId: Int?,
        dailyValue: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                employeeRepository.createEmployee(name, surname, roleId, workId, dailyValue)
                employees = employeeRepository.getAllEmployees()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to create employee: ${e.message}"
                println("Error in createEmployee: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    fun updateEmployee(
        id: Int,
        name: String,
        surname: String,
        roleId: Int,
        workId: Int?,
        dailyValue: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                employeeRepository.updateEmployee(id, name, surname, roleId, workId, dailyValue)
                employees = employeeRepository.getAllEmployees()
                payrolls = payrollRepository.getAllPayrolls()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to update employee: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                employeeRepository.deleteEmployee(id)
                employees = employeeRepository.getAllEmployees()
                payrolls = payrollRepository.getAllPayrolls()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to delete employee: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Day adjustment operations
    fun createAdjustment(
        employeeId: Int,
        date: String,
        adjustmentValue: String,
        notes: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                dayAdjustmentRepository.createAdjustment(
                    employeeId,
                    date,
                    adjustmentValue,
                    notes.ifBlank { null }
                )
                dayAdjustments = dayAdjustmentRepository.getAllAdjustments()
                payrolls = payrollRepository.getAllPayrolls()
                showAddAdjustmentDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create adjustment: ${e.message}"
                println("Error in createAdjustment: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    fun createDayAdjustment(
        employeeId: Int,
        date: String,
        adjustmentValue: String,
        notes: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                dayAdjustmentRepository.createAdjustment(
                    employeeId,
                    date,
                    adjustmentValue,
                    notes.ifBlank { null }
                )
                dayAdjustments = dayAdjustmentRepository.getAllAdjustments()
                payrolls = payrollRepository.getAllPayrolls()
                successMessage = "Adjustment added successfully!"
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to create adjustment: ${e.message}"
                println("Error in createDayAdjustment: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteAdjustment(adjustmentId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                dayAdjustmentRepository.deleteAdjustment(adjustmentId)
                dayAdjustments = dayAdjustmentRepository.getAllAdjustments()
                payrolls = payrollRepository.getAllPayrolls()
                successMessage = "Adjustment deleted successfully!"
            } catch (e: Exception) {
                errorMessage = "Failed to delete adjustment: ${e.message}"
                println("Error in deleteAdjustment: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

    // Payroll operations
    fun generatePayroll(periodStartDate: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                payrollRepository.generatePayroll(periodStartDate)
                payrolls = payrollRepository.getAllPayrolls()
                showGeneratePayrollDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to generate payroll: ${e.message}"
                println("Error in generatePayroll: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }
}
