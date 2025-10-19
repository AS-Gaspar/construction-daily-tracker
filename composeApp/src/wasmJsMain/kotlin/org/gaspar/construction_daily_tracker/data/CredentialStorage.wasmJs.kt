package org.gaspar.construction_daily_tracker.data

/**
 * WasmJS implementation of CredentialStorage.
 * Uses in-memory storage (credentials lost on page reload).
 * TODO: Enhance with localStorage for persistence.
 */
actual class CredentialStorage {

    private var apiKey: String? = null
    private var serverUrl: String = "http://localhost:8080"

    actual fun saveApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    actual fun getApiKey(): String? {
        return apiKey
    }

    actual fun hasApiKey(): Boolean {
        return apiKey != null
    }

    actual fun saveServerUrl(serverUrl: String) {
        this.serverUrl = serverUrl
    }

    actual fun getServerUrl(): String {
        return serverUrl
    }

    actual fun hasServerUrl(): Boolean {
        return serverUrl.isNotBlank()
    }

    actual fun clearAll() {
        apiKey = null
        serverUrl = "http://localhost:8080"
    }
}
