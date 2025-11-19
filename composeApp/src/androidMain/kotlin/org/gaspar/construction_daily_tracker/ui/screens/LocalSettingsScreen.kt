package org.gaspar.construction_daily_tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.gaspar.construction_daily_tracker.i18n.Language
import org.gaspar.construction_daily_tracker.i18n.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: Language,
    strings: Strings,
    onBack: () -> Unit,
    onLanguageChange: (Language) -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Language Settings",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = { showLanguageDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Language: ${currentLanguage.displayName}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "App Version: 1.0 (Local)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Language") },
                text = {
                    Column {
                        Language.entries.forEach { language ->
                            OutlinedButton(
                                onClick = {
                                    onLanguageChange(language)
                                    showLanguageDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(language.displayName)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
