package org.gaspar.construction_daily_tracker.data

import androidx.compose.runtime.Composable

/**
 * Platform-specific factory function to create CredentialStorage instance.
 * On Android, this uses the application context.
 * On Web, this creates an in-memory instance.
 */
@Composable
expect fun rememberCredentialStorage(): CredentialStorage
