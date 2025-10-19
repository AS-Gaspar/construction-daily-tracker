package org.gaspar.construction_daily_tracker.data

/**
 * Platform-agnostic interface for secure credential storage.
 * Provides methods to save and retrieve API credentials.
 */
expect class CredentialStorage {

    /**
     * Saves the API key securely.
     */
    fun saveApiKey(apiKey: String)

    /**
     * Retrieves the stored API key.
     * @return The API key, or null if not set
     */
    fun getApiKey(): String?

    /**
     * Checks if an API key has been configured.
     */
    fun hasApiKey(): Boolean

    /**
     * Saves the API server URL.
     */
    fun saveServerUrl(serverUrl: String)

    /**
     * Retrieves the stored server URL.
     * @return The server URL, or default value if not set
     */
    fun getServerUrl(): String

    /**
     * Checks if server URL has been configured.
     */
    fun hasServerUrl(): Boolean

    /**
     * Clears all stored credentials.
     */
    fun clearAll()
}
