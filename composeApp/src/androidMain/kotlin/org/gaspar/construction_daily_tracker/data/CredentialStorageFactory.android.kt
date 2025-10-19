package org.gaspar.construction_daily_tracker.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of rememberCredentialStorage.
 * Uses the application context to ensure the storage persists across activity recreations.
 */
@Composable
actual fun rememberCredentialStorage(): CredentialStorage {
    val context = LocalContext.current.applicationContext
    return remember { CredentialStorage(context) }
}
