package org.gaspar.construction_daily_tracker.navigation

import androidx.compose.runtime.*

/**
 * Navigation state holder for managing screen navigation.
 */
class NavigationState {
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    private val backStack = mutableStateListOf<Screen>()

    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
            true
        } else {
            false
        }
    }

    fun canNavigateBack(): Boolean = backStack.isNotEmpty()
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}
