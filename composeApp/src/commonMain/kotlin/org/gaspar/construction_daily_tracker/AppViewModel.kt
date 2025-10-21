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

    var successMessage by mutableStateOf<String?>(null)
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
     * Test connection to the API server without saving credentials.
     */
    fun testConnection() {
        val serverUrl = credentialStorage.getServerUrl()
        val apiKey = credentialStorage.getApiKey()

        println("🔍 TEST CONNECTION START")
        println("   Server URL: $serverUrl")
        println("   API Key: ${apiKey?.take(10)}...${apiKey?.takeLast(4)}")

        if (apiKey == null) {
            errorMessage = "Please enter an API key first"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                println("   Creating HTTP client...")
                val testClient = ApiClient.create(serverUrl, apiKey)
                val testRepo = WorkRepository(testClient)

                println("   Sending request to /works...")
                // Try to fetch works as a connection test
                val works = testRepo.getAllWorks()

                println("✅ CONNECTION TEST SUCCESSFUL!")
                println("   Received ${works.size} works from server")
                errorMessage = "✅ Connection successful!\n\nReceived ${works.size} works from server.\n\nYou can now use the app."
            } catch (e: Exception) {
                println("❌ CONNECTION TEST FAILED!")
                println("   Error type: ${e::class.simpleName}")
                println("   Error message: ${e.message}")
                println("   Full stack trace:")
                println(e.stackTraceToString())

                // Provide specific error messages based on error type
                errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "❌ Cannot reach server\n\nThe URL cannot be resolved:\n$serverUrl\n\n" +
                        "Check:\n• Server is running\n• URL is correct\n• You're on the same network (for physical device)"

                    e.message?.contains("timeout") == true ->
                        "❌ Connection timeout\n\nServer: $serverUrl\n\n" +
                        "Check:\n• Server is running\n• Firewall isn't blocking\n• Try: http://10.0.2.2:8080 (emulator)\n• Try: http://192.168.3.117:8080 (physical)"

                    e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true ->
                        "❌ Authentication failed\n\nThe API key is invalid.\n\n" +
                        "Check:\n• API key is exactly 64 characters\n• No extra spaces\n• Matches server's API_KEY in .env.local"

                    e.message?.contains("Connection refused") == true ->
                        "❌ Connection refused\n\nServer: $serverUrl\n\n" +
                        "The server is not running or not accepting connections.\n\n" +
                        "Start server: ./start-server.sh"

                    else ->
                        "❌ Connection failed\n\nError: ${e.message}\n\nServer: $serverUrl\n\n" +
                        "Check Android Studio Logcat for details."
                }
            } finally {
                isLoading = false
                println("🔍 TEST CONNECTION END\n")
            }
        }
    }

    /**
     * Refresh all data from the server.
     */
    fun refreshAll() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                println("🔄 REFRESH ALL DATA START")
                works = workRepository?.getAllWorks() ?: emptyList()
                println("   ✓ Loaded ${works.size} works")
                roles = roleRepository?.getAllRoles() ?: emptyList()
                println("   ✓ Loaded ${roles.size} roles")
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                println("   ✓ Loaded ${employees.size} employees")
                dayAdjustments = dayAdjustmentRepository?.getAllAdjustments() ?: emptyList()
                println("   ✓ Loaded ${dayAdjustments.size} day adjustments")
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
                println("   ✓ Loaded ${payrolls.size} payrolls")
                println("✅ REFRESH ALL DATA SUCCESS\n")
            } catch (e: Exception) {
                println("❌ REFRESH ALL DATA FAILED: ${e.message}")
                println(e.stackTraceToString())
                errorMessage = "Failed to load data: ${e.message}\n\nGo to Settings to check your connection."
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
                println("API connection error in createWork: ${e.stackTraceToString()}")
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
                workRepository?.updateWork(id, name)
                works = workRepository?.getAllWorks() ?: emptyList()
                successMessage = "Work updated successfully!"
            } catch (e: Exception) {
                errorMessage = "Failed to update work: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearSuccessMessage() {
        successMessage = null
    }

    fun deleteWork(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Delete the work (database will automatically set employees' workId to null via ON DELETE SET NULL)
                workRepository?.deleteWork(id)

                // Refresh data
                works = workRepository?.getAllWorks() ?: emptyList()
                employees = employeeRepository?.getAllEmployees() ?: emptyList()

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
                employeeRepository?.assignToWork(employeeId, workId)
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
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
                roleRepository?.createRole(title)
                roles = roleRepository?.getAllRoles() ?: emptyList()
                showAddRoleDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to create role: ${e.message}"
                println("API connection error in createRole: ${e.stackTraceToString()}")
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
                employeeRepository?.createEmployee(name, surname, roleId, workId, dailyValue)
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                navigationState.navigateBack()
            } catch (e: Exception) {
                errorMessage = "Failed to create employee: ${e.message}"
                println("API connection error in createEmployee: ${e.stackTraceToString()}")
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
                employeeRepository?.updateEmployee(id, name, surname, roleId, workId, dailyValue)
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                // Also refresh payrolls as employee changes might affect them
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
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
                employeeRepository?.deleteEmployee(id)
                employees = employeeRepository?.getAllEmployees() ?: emptyList()
                // Also refresh payrolls as employee deletion might affect them
                payrolls = payrollRepository?.getAllPayrolls() ?: emptyList()
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
                println("API connection error in createAdjustment: ${e.stackTraceToString()}")
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
                println("API connection error in generatePayroll: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }
}
