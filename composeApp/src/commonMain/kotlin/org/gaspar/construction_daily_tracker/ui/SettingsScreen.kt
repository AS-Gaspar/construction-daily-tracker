package org.gaspar.construction_daily_tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

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
    onSave: (serverUrl: String, apiKey: String) -> Unit,
    onBack: () -> Unit
) {
    var serverUrl by remember { mutableStateOf(currentServerUrl) }
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var showApiKey by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
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
            Text(
                text = "Configure your API connection",
                style = MaterialTheme.typography.bodyLarge
            )

            // Server URL input
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
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
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                placeholder = { Text("Enter your API key") },
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
                }
            )

            if (saveSuccess) {
                Text(
                    text = "✓ Settings saved successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (serverUrl.isNotBlank() && apiKey.isNotBlank()) {
                        onSave(serverUrl.trim(), apiKey.trim())
                        saveSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = serverUrl.isNotBlank() && apiKey.isNotBlank()
            ) {
                Text("Save Settings")
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
