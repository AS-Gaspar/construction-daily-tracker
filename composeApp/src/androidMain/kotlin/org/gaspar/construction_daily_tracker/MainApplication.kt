package org.gaspar.construction_daily_tracker

import android.app.Application
import org.gaspar.construction_daily_tracker.data.ApiKeyManager

/**
 * Application class for initialization.
 */
class MainApplication : Application() {

    lateinit var apiKeyManager: ApiKeyManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        apiKeyManager = ApiKeyManager(this)
    }

    companion object {
        lateinit var instance: MainApplication
            private set

        fun getApiKeyManager(): ApiKeyManager {
            return instance.apiKeyManager
        }
    }
}
