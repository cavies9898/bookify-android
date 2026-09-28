package com.cavies.bookify.ui.screen.admin.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cavies.bookify.R
import com.cavies.bookify.navigation.AppChromeState
import com.cavies.bookify.navigation.BottomBarConfig
import com.cavies.bookify.ui.screen.admin.bookings.AdminBookingsScreen
import com.cavies.bookify.ui.screen.admin.mappicker.MapPickerScreen
import com.cavies.bookify.ui.screen.admin.serviceform.AdminServiceFormScreen
import com.cavies.bookify.ui.screen.admin.servicelist.AdminServiceListScreen
import com.cavies.bookify.ui.screen.client.profile.ProfileScreen

@Composable
fun AdminHomeScreen(
    onNavigateToLogin: () -> Unit,
    chromeState: AppChromeState
) {
    val innerNavController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabServices = stringResource(R.string.admin_tab_services)
    val tabBookings = stringResource(R.string.admin_tab_bookings)
    val tabProfile = stringResource(R.string.admin_tab_profile)
    val tabs = listOf(tabServices, tabBookings, tabProfile)
    val icons = listOf(Icons.Default.Build, Icons.Default.CalendarMonth, Icons.Default.Person)
    val routes = listOf("admin_services", "admin_bookings", "admin_profile")

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when {
        currentRoute == "admin_services" -> tabServices
        currentRoute == "admin_service_form" -> stringResource(R.string.admin_service_form_new_title)
        currentRoute?.startsWith("admin_service_form/") == true -> stringResource(R.string.admin_service_form_edit_title)
        currentRoute == "admin_map_picker" -> stringResource(R.string.admin_map_picker_title)
        currentRoute == "admin_bookings" -> tabBookings
        currentRoute == "admin_profile" -> tabProfile
        else -> stringResource(R.string.admin_home_title)
    }

    val showBackButton = currentRoute != "admin_services" &&
            currentRoute != "admin_bookings" &&
            currentRoute != "admin_profile"

    val isMapPicker = currentRoute == "admin_map_picker"
    val showBottomBar = currentRoute in routes
    val showFab = currentRoute == "admin_services"

    LaunchedEffect(title, showBackButton, showBottomBar, showFab, selectedTab, isMapPicker) {
        chromeState.title.value = title
        chromeState.useAppHeader.value = !isMapPicker
        chromeState.showBackButton.value = showBackButton
        chromeState.onBackClick.value = if (showBackButton) {{ innerNavController.popBackStack() }} else null
        chromeState.showBottomBar.value = showBottomBar
        chromeState.bottomBarItems.value = tabs.zip(icons).map { (label, icon) ->
            BottomBarConfig(label, icon, routes[tabs.indexOf(label)])
        }
        chromeState.selectedTab.intValue = selectedTab
        chromeState.onTabSelected.value = { index ->
            selectedTab = index
            innerNavController.navigate(routes[index]) {
                popUpTo(routes[0]) { inclusive = index == 0 }
            }
        }
        chromeState.showFab.value = showFab
        chromeState.onFabClick.value = {
            innerNavController.navigate("admin_service_form")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chromeState.title.value = "Bookify"
            chromeState.showBackButton.value = false
            chromeState.showBottomBar.value = false
            chromeState.showFab.value = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = innerNavController,
            startDestination = "admin_services"
        ) {
            composable("admin_services") {
                AdminServiceListScreen(navController = innerNavController)
            }
            composable("admin_service_form") {
                AdminServiceFormScreen(
                    serviceId = null,
                    navController = innerNavController
                )
            }
            composable(
                "admin_service_form/{serviceId}",
                arguments = listOf(navArgument("serviceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getLong("serviceId")
                AdminServiceFormScreen(
                    serviceId = serviceId,
                    navController = innerNavController
                )
            }
            composable("admin_bookings") {
                AdminBookingsScreen()
            }
            composable("admin_map_picker") {
                MapPickerScreen(
                    onLocationSelected = { lat, lng ->
                        innerNavController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("map_result_lat", "%.6f".format(lat))
                        innerNavController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("map_result_lng", "%.6f".format(lng))
                        innerNavController.popBackStack()
                    },
                    onBack = { innerNavController.popBackStack() }
                )
            }
            composable("admin_profile") {
                ProfileScreen(onLogout = onNavigateToLogin)
            }
        }
    }
}
