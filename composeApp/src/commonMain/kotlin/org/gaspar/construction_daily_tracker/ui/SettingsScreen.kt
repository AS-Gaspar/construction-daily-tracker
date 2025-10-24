package org.gaspar.construction_daily_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.i18n.Language
import org.gaspar.construction_daily_tracker.i18n.Strings

// Tailwind blue-600
private val TailwindBlue = Color(0xFF2563EB)

/**
 * Settings screen for configuring API connection.
 *
 * @param currentServerUrl Current server URL or empty if not set
 * @param currentApiKey Current API key or empty if not set (masked for security)
 * @param onSave Callback when settings are saved
 * @param onBack Callback to navigate back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentServerUrl: String = "",
    currentApiKey: String = "",
    currentLanguage: Language = Language.ENGLISH,
    strings: Strings,
    errorMessage: String? = null,
    successMessage: String? = null,
    isLoading: Boolean = false,
    showBackButton: Boolean = true,
    onSave: (serverUrl: String, apiKey: String) -> Unit,
    onBack: () -> Unit,
    onTestConnection: (() -> Unit)? = null,
    onLanguageChange: (Language) -> Unit = {}
) {
    var serverUrl by remember { mutableStateOf(currentServerUrl) }
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var showApiKey by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    // Reset success message when error occurs
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            saveSuccess = false
        }
    }

    // Reset saveSuccess when successMessage appears
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            saveSuccess = false
        }
    }

    var showLanguageDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settingsTitle) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TailwindBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Section
            Text(
                text = strings.languageSettings,
                style = MaterialTheme.typography.titleMedium,
                color = TailwindBlue
            )

            ExposedDropdownMenuBox(
                expanded = showLanguageDropdown,
                onExpandedChange = { showLanguageDropdown = !showLanguageDropdown }
            ) {
                OutlinedTextField(
                    value = currentLanguage.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(strings.selectLanguage) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLanguageDropdown) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showLanguageDropdown,
                    onDismissRequest = { showLanguageDropdown = false }
                ) {
                    Language.entries.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language.displayName) },
                            onClick = {
                                onLanguageChange(language)
                                showLanguageDropdown = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // API Settings Section
            Text(
                text = strings.apiSettings,
                style = MaterialTheme.typography.titleMedium,
                color = TailwindBlue
            )

            // Server URL input
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text(strings.serverUrl) },
                placeholder = { Text("http://your-server.com:8080") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Example: http://192.168.1.100:8080 or http://10.0.2.2:8080 (emulator)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // API Key input
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it.trim() }, // Auto-trim whitespace
                label = { Text(strings.apiKey) },
                placeholder = { Text("Paste your API key here") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showApiKey) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { showApiKey = !showApiKey }) {
                        Text(if (showApiKey) "Hide" else "Show")
                    }
                },
                supportingText = {
                    Text(
                        text = "64 characters long - paste carefully",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )

            // Error message display
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌ Connection Error",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Success message display
            successMessage?.let { success ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✓ Success",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = success,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Settings saved confirmation (separate from connection test)
            if (saveSuccess && errorMessage == null && successMessage == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "✓ Settings saved and connected successfully!",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Test Connection button
            onTestConnection?.let { testFn ->
                OutlinedButton(
                    onClick = testFn,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && serverUrl.isNotBlank() && apiKey.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(strings.testConnection)
                }
            }

            // Save button
            Button(
                onClick = {
                    if (serverUrl.isNotBlank() && apiKey.isNotBlank()) {
                        onSave(serverUrl.trim(), apiKey.trim())
                        saveSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = serverUrl.isNotBlank() && apiKey.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(strings.saveSettings)
            }

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ℹ️ Important",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "• Your API key is stored securely using Android Keystore encryption\n" +
                                "• Make sure your server is running and accessible\n" +
                                "• Use your device's local IP for physical devices\n" +
                                "• Use 10.0.2.2 for Android emulator (maps to host machine's localhost)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
