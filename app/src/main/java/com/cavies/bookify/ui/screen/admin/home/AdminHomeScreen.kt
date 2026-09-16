package com.cavies.bookify.ui.screen.admin.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

    val tabs = listOf("Servicios", "Reservas", "Perfil")
    val icons = listOf(Icons.Default.Build, Icons.Default.CalendarMonth, Icons.Default.Person)
    val routes = listOf("admin_services", "admin_bookings", "admin_profile")

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when {
        currentRoute == "admin_services" -> "Servicios"
        currentRoute == "admin_service_form" -> "Nuevo Servicio"
        currentRoute?.startsWith("admin_service_form/") == true -> "Editar Servicio"
        currentRoute == "admin_map_picker" -> "Seleccionar ubicación"
        currentRoute == "admin_bookings" -> "Reservas"
        currentRoute == "admin_profile" -> "Perfil"
        else -> "Bookify Admin"
    }

    val showBackButton = currentRoute != "admin_services" &&
            currentRoute != "admin_bookings" &&
            currentRoute != "admin_profile"

    val showBottomBar = currentRoute in routes
    val showFab = currentRoute == "admin_services"

    LaunchedEffect(title, showBackButton, showBottomBar, showFab, selectedTab) {
        chromeState.title.value = title
        chromeState.showBackButton.value = showBackButton
        chromeState.onBackClick.value = { innerNavController.popBackStack() }
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
