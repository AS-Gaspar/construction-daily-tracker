package org.gaspar.construction_daily_tracker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages secure storage of API key using Android's EncryptedSharedPreferences.
 * Uses AES256-GCM encryption backed by Android Keystore.
 */
class ApiKeyManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_api_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Saves the API key securely.
     * @param apiKey The API key to store
     */
    fun saveApiKey(apiKey: String) {
        encryptedPrefs.edit()
            .putString(KEY_API_KEY, apiKey)
            .apply()
    }

    /**
     * Retrieves the stored API key.
     * @return The API key, or null if not set
     */
    fun getApiKey(): String? {
        return encryptedPrefs.getString(KEY_API_KEY, null)
    }

    /**
     * Checks if an API key has been configured.
     * @return true if API key exists, false otherwise
     */
    fun hasApiKey(): Boolean {
        return getApiKey() != null
    }

    /**
     * Saves the API server URL.
     * @param serverUrl The server URL (e.g., "https://your-server.com:8080")
     */
    fun saveServerUrl(serverUrl: String) {
        encryptedPrefs.edit()
            .putString(KEY_SERVER_URL, serverUrl)
            .apply()
    }

    /**
     * Retrieves the stored server URL.
     * @return The server URL, or default value if not set
     */
    fun getServerUrl(): String {
        return encryptedPrefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }

    /**
     * Clears all stored credentials.
     */
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }

    /**
     * Saves the user's preferred language code.
     * @param languageCode The language code (e.g., "en", "pt")
     */
    fun saveLanguage(languageCode: String) {
        encryptedPrefs.edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }

    /**
     * Retrieves the stored language code.
     * @return The language code, or "en" as default
     */
    fun getLanguage(): String {
        return encryptedPrefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    companion object {
        private const val KEY_API_KEY = "api_key"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_LANGUAGE = "language"
        private const val DEFAULT_SERVER_URL = "http://10.0.2.2:8080" // Android emulator localhost
        private const val DEFAULT_LANGUAGE = "en"
    }
}
