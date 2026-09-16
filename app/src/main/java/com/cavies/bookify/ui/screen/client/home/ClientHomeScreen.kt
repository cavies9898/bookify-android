package com.cavies.bookify.ui.screen.client.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
import com.cavies.bookify.ui.screen.client.availability.AvailabilityScreen
import com.cavies.bookify.ui.screen.client.bookingdetail.BookingDetailScreen
import com.cavies.bookify.ui.screen.client.bookinglist.BookingListScreen
import com.cavies.bookify.ui.screen.client.createbooking.CreateBookingScreen
import com.cavies.bookify.ui.screen.client.profile.ProfileScreen
import com.cavies.bookify.ui.screen.client.servicedetail.ServiceDetailScreen
import com.cavies.bookify.ui.screen.client.servicelist.ClientServiceListScreen

@Composable
fun ClientHomeScreen(
    onNavigateToLogin: () -> Unit,
    chromeState: AppChromeState
) {
    val innerNavController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Servicios", "Mis Reservas", "Perfil")
    val icons = listOf(Icons.Default.List, Icons.Default.CalendarMonth, Icons.Default.Person)
    val routes = listOf("client_services", "client_bookings", "client_profile")

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentArgs = navBackStackEntry?.arguments

    val title = when {
        currentRoute == "client_services" -> "Servicios"
        currentRoute == "client_service_detail/{serviceId}" -> "Detalle"
        currentRoute == "client_availability/{serviceId}/{serviceName}" -> {
            currentArgs?.getString("serviceName") ?: "Disponibilidad"
        }
        currentRoute == "client_create_booking/{serviceId}/{serviceName}/{startAt}/{endAt}" -> {
            currentArgs?.getString("serviceName") ?: "Confirmar Reserva"
        }
        currentRoute == "client_bookings" -> "Mis Reservas"
        currentRoute == "client_booking_detail/{bookingId}" -> "Detalle de Reserva"
        currentRoute == "client_profile" -> "Perfil"
        else -> "Bookify"
    }

    val showBackButton = currentRoute != "client_services" &&
            currentRoute != "client_bookings" &&
            currentRoute != "client_profile"

    val showBottomBar = currentRoute in routes

    LaunchedEffect(title, showBackButton, showBottomBar, selectedTab) {
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
        chromeState.showFab.value = false
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
            startDestination = "client_services"
        ) {
            composable("client_services") {
                ClientServiceListScreen(navController = innerNavController)
            }
            composable(
                "client_service_detail/{serviceId}",
                arguments = listOf(navArgument("serviceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getLong("serviceId") ?: 0L
                ServiceDetailScreen(
                    serviceId = serviceId,
                    navController = innerNavController
                )
            }
            composable(
                "client_availability/{serviceId}/{serviceName}",
                arguments = listOf(
                    navArgument("serviceId") { type = NavType.LongType },
                    navArgument("serviceName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getLong("serviceId") ?: 0L
                val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
                AvailabilityScreen(
                    serviceId = serviceId,
                    serviceName = serviceName,
                    navController = innerNavController
                )
            }
            composable(
                "client_create_booking/{serviceId}/{serviceName}/{startAt}/{endAt}",
                arguments = listOf(
                    navArgument("serviceId") { type = NavType.LongType },
                    navArgument("serviceName") { type = NavType.StringType },
                    navArgument("startAt") { type = NavType.StringType },
                    navArgument("endAt") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                CreateBookingScreen(
                    serviceId = backStackEntry.arguments?.getLong("serviceId") ?: 0L,
                    serviceName = backStackEntry.arguments?.getString("serviceName") ?: "",
                    startAt = backStackEntry.arguments?.getString("startAt") ?: "",
                    endAt = backStackEntry.arguments?.getString("endAt") ?: "",
                    navController = innerNavController
                )
            }
            composable("client_bookings") {
                BookingListScreen(navController = innerNavController)
            }
            composable(
                "client_booking_detail/{bookingId}",
                arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
                BookingDetailScreen(
                    bookingId = bookingId,
                    navController = innerNavController
                )
            }
            composable("client_profile") {
                ProfileScreen(onLogout = onNavigateToLogin)
            }
        }
    }
}
