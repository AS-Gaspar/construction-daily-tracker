package org.gaspar.construction_daily_tracker.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * WasmJS implementation of rememberCredentialStorage.
 * Creates an in-memory storage instance.
 */
@Composable
actual fun rememberCredentialStorage(): CredentialStorage {
    return remember { CredentialStorage() }
}
