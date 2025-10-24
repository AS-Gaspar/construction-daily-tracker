package org.gaspar.construction_daily_tracker.data

import android.content.Context

/**
 * Android implementation of CredentialStorage using ApiKeyManager.
 */
actual class CredentialStorage(context: Context) {

    private val apiKeyManager = ApiKeyManager(context)

    actual fun saveApiKey(apiKey: String) {
        apiKeyManager.saveApiKey(apiKey)
    }

    actual fun getApiKey(): String? {
        return apiKeyManager.getApiKey()
    }

    actual fun hasApiKey(): Boolean {
        return apiKeyManager.hasApiKey()
    }

    actual fun saveServerUrl(serverUrl: String) {
        apiKeyManager.saveServerUrl(serverUrl)
    }

    actual fun getServerUrl(): String {
        return apiKeyManager.getServerUrl()
    }

    actual fun hasServerUrl(): Boolean {
        val url = getServerUrl()
        // Check if it's not the default or empty
        return url.isNotBlank() && url != "http://10.0.2.2:8080"
    }

    actual fun clearAll() {
        apiKeyManager.clearAll()
    }

    actual fun saveLanguage(languageCode: String) {
        apiKeyManager.saveLanguage(languageCode)
    }

    actual fun getLanguage(): String {
        return apiKeyManager.getLanguage()
    }
}
