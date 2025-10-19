package org.gaspar.construction_daily_tracker

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import kotlinx.coroutines.launch
import org.gaspar.construction_daily_tracker.data.ApiKeyManager
import org.gaspar.construction_daily_tracker.model.Work
import org.gaspar.construction_daily_tracker.network.ApiClient
import org.gaspar.construction_daily_tracker.network.WorkRepository

/**
 * EXAMPLE: How to use the API key manager and HTTP client in your app.
 *
 * This file demonstrates the complete flow from storing credentials
 * to making authenticated API calls.
 */

// ============================================================================
// STEP 1: Initialize ApiKeyManager (usually in Application or Activity)
// ============================================================================

fun initializeApiKeyManager(context: Context): ApiKeyManager {
    return ApiKeyManager(context)
}

// ============================================================================
// STEP 2: Save API credentials (from Settings Screen)
// ============================================================================

fun saveCredentials(context: Context, serverUrl: String, apiKey: String) {
    val apiKeyManager = ApiKeyManager(context)
    apiKeyManager.saveServerUrl(serverUrl)
    apiKeyManager.saveApiKey(apiKey)
}

// ============================================================================
// STEP 3: Create HTTP client with stored credentials
// ============================================================================

fun createAuthenticatedClient(apiKeyManager: ApiKeyManager): HttpClient? {
    val apiKey = apiKeyManager.getApiKey()
    val serverUrl = apiKeyManager.getServerUrl()

    return if (apiKey != null) {
        ApiClient.create(serverUrl, apiKey)
    } else {
        null // User needs to configure API key first
    }
}

// ============================================================================
// STEP 4: Use in ViewModel (recommended pattern)
// ============================================================================

class WorksViewModel(private val apiKeyManager: ApiKeyManager) : ViewModel() {

    private var client: HttpClient? = null
    private var repository: WorkRepository? = null

    var works by mutableStateOf<List<Work>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        initializeClient()
    }

    private fun initializeClient() {
        client = createAuthenticatedClient(apiKeyManager)
        client?.let {
            repository = WorkRepository(it)
        }
    }

    fun loadWorks() {
        if (repository == null) {
            error = "API not configured. Please set up your API key in settings."
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                works = repository!!.getAllWorks()
            } catch (e: Exception) {
                error = "Failed to load works: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createWork(name: String) {
        if (repository == null) {
            error = "API not configured"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                repository!!.createWork(name)
                loadWorks() // Reload list
            } catch (e: Exception) {
                error = "Failed to create work: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client?.close()
    }
}

// ============================================================================
// STEP 5: Use in Composable (example)
// ============================================================================

@Composable
fun ExampleWorkListScreen(apiKeyManager: ApiKeyManager) {
    // In real app, use remember with proper ViewModel factory
    val viewModel = remember { WorksViewModel(apiKeyManager) }

    LaunchedEffect(Unit) {
        viewModel.loadWorks()
    }

    when {
        viewModel.isLoading -> {
            // Show loading indicator
        }
        viewModel.error != null -> {
            // Show error message
            // Text(viewModel.error ?: "")
        }
        else -> {
            // Show works list
            // LazyColumn { items(viewModel.works) { ... } }
        }
    }
}

// ============================================================================
// STEP 6: Check if API is configured (for showing settings screen)
// ============================================================================

fun isApiConfigured(apiKeyManager: ApiKeyManager): Boolean {
    return apiKeyManager.hasApiKey()
}

// ============================================================================
// STEP 7: Clear credentials (logout functionality)
// ============================================================================

fun clearCredentials(apiKeyManager: ApiKeyManager) {
    apiKeyManager.clearAll()
}
