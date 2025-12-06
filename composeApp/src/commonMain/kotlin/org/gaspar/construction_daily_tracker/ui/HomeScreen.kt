package org.gaspar.construction_daily_tracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gaspar.construction_daily_tracker.i18n.Strings
import org.gaspar.construction_daily_tracker.navigation.Screen

data class MenuItem(
    val title: String,
    val screen: Screen,
    val icon: String,
    val description: String
)

/**
 * Home screen with menu cards for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    strings: Strings,
    onNavigate: (Screen) -> Unit
) {
    // Main menu items including Roles
    val menuItems = listOf(
        MenuItem(strings.worksTitle, Screen.Works, "🏗️", strings.worksDescription),
        MenuItem(strings.employeesTitle, Screen.Employees, "👷", strings.employeesDescription),
        MenuItem(strings.rolesTitle, Screen.Roles, "🔧", "Gerenciar funções de trabalho"),
        MenuItem(strings.payrollTitle, Screen.Payroll, "💰", strings.payrollDescription),
        MenuItem(strings.unassignedEmployees, Screen.UnassignedEmployees, "⚠️", strings.unassignedEmployeesDescription)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.appName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Tailwind blue-600: #2563eb
                    containerColor = Color(0xFF2563EB),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(menuItems) { item ->
                MenuCard(
                    title = item.title,
                    icon = item.icon,
                    description = item.description,
                    onClick = { onNavigate(item.screen) }
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 16.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 13.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
