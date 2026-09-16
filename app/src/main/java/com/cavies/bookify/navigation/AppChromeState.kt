package com.cavies.bookify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
class AppChromeState {
    val title = mutableStateOf("Bookify")
    val showBackButton = mutableStateOf(false)
    val onBackClick = mutableStateOf<(() -> Unit)?>(null)
    val showBottomBar = mutableStateOf(false)
    val bottomBarItems = mutableStateOf<List<BottomBarConfig>>(emptyList())
    val selectedTab = mutableIntStateOf(0)
    val onTabSelected = mutableStateOf<(Int) -> Unit>({})
    val showFab = mutableStateOf(false)
    val onFabClick = mutableStateOf<(() -> Unit)?>(null)
}

data class BottomBarConfig(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun rememberAppChromeState(): AppChromeState {
    return remember { AppChromeState() }
}
