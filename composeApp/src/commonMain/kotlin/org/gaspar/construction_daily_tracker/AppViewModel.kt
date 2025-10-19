package org.gaspar.construction_daily_tracker

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import kotlinx.coroutines.launch
import org.gaspar.construction_daily_tracker.data.CredentialStorage
import org.gaspar.construction_daily_tracker.model.*
import org.gaspar.construction_daily_tracker.navigation.NavigationState
import org.gaspar.construction_daily_tracker.navigation.Screen
import org.gaspar.construction_daily_tracker.network.*

/**
 * Main ViewModel for the app, managing all data and navigation.
 */
class AppViewModel(private val credentialStorage: CredentialStorage) : ViewModel() {

    // Navigation
    val navigationState = NavigationState()

    // API Client (will be initialized after settings are configured)
    private var httpClient: HttpClient? = null
    private var workRepository: WorkRepository? = null
    private var roleRepository: RoleRepository? = null
    private var employeeRepository: EmployeeRepository? = null
    private var dayAdjustmentRepository: DayAdjustmentRepository? = null
    private var payrollRepository: PayrollRepository? = null

    // State
    var isApiConfigured by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // Try to load saved credentials and auto-configure
        loadSavedCredentials()
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
     * Load saved credentials from secure storage and auto-configure if available.
     */
    private fun loadSavedCredentials() {
        if (credentialStorage.hasApiKey() && credentialStorage.hasServerUrl()) {
            val serverUrl = credentialStorage.getServerUrl()
            val apiKey = credentialStorage.getApiKey()
            if (apiKey != null) {
                configureApi(serverUrl, apiKey, saveCredentials = false)
            }
        }
    }

    /**
     * Configure API client with server URL and API key.
     * @param saveCredentials If true, saves credentials to secure storage
     */
    fun configureApi(serverUrl: String, apiKey: String, saveCredentials: Boolean = true) {
        // Save credentials to secure storage
        if (saveCredentials) {
            credentialStorage.saveServerUrl(serverUrl)
            credentialStorage.saveApiKey(apiKey)
        }

        // Create HTTP client and repositories
        httpClient = ApiClient.create(serverUrl, apiKey)
        workRepository = WorkRepository(httpClient!!)
        roleRepository = RoleRepository(httpClient!!)
        employeeRepository = EmployeeRepository(httpClient!!)
        dayAdjustmentRepository = DayAdjustmentRepository(httpClient!!)
        payrollRepository = PayrollRepository(httpClient!!)
        isApiConfigured = true

        // Load initial data
        refreshAll()
    }

    /**
     * Get current server URL from storage.
     */
    fun getCurrentServerUrl(): String {
        return credentialStorage.getServerUrl()
    }

    /**
     * Get current API key from storage (for display in settings).
     */
    fun getCurrentApiKey(): String {
        return credentialStorage.getApiKey() ?: ""
    }

    /**
     * Refresh all data from the server.
     */
    fun refreshAll() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                works = workRepository?.getAllWorks() ?: emptyList()
                roles = roleRepository?.getAllRoles() ?: emptyList()
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                dayAdjustments = dayAdjustmentRepository?.getAllAdjustments() ?: emptyList()
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Failed to load data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Work operations
    fun createWork(name: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                workRepository?.createWork(name)
                works = workRepository?.getAllWorks() ?: emptyList()
                showAddWorkDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create work: ${e.message}"
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
                roleRepository?.createRole(title)
                roles = roleRepository?.getAllRoles() ?: emptyList()
                showAddRoleDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create role: ${e.message}"
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
        workId: Int,
        dailyValue: String
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                employeeRepository?.createEmployee(name, surname, roleId, workId, dailyValue)
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to create employee: ${e.message}"
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
                dayAdjustmentRepository?.createAdjustment(
                    employeeId,
                    date,
                    adjustmentValue,
                    notes.ifBlank { null }
                )
                dayAdjustments = dayAdjustmentRepository?.getAllAdjustments() ?: emptyList()
                // Also refresh payrolls as they auto-update
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
                showAddAdjustmentDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create adjustment: ${e.message}"
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
                payrollRepository?.generatePayroll(periodStartDate)
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
                showGeneratePayrollDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to generate payroll: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
