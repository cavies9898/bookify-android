package com.cavies.bookify.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cavies.bookify.core.domain.repository.AuthRepository
import com.cavies.bookify.ui.screen.admin.home.AdminHomeScreen
import com.cavies.bookify.ui.screen.auth.login.LoginScreen
import com.cavies.bookify.ui.screen.auth.passwordrecovery.PasswordRecoveryScreen
import com.cavies.bookify.ui.screen.auth.register.RegisterScreen
import com.cavies.bookify.ui.screen.auth.resetpassword.ResetPasswordScreen
import com.cavies.bookify.ui.screen.client.home.ClientHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _startDestination.value = when {
                user != null && user.role.name == "ADMIN" -> "admin_tabs"
                user != null -> "client_tabs"
                else -> "login"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookifyNavHost(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination by viewModel.startDestination.collectAsState()
    val chromeState = rememberAppChromeState()

    startDestination?.let { dest ->
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (chromeState.showBottomBar.value || chromeState.title.value != "Bookify") {
                    TopAppBar(
                        title = { Text(chromeState.title.value) },
                        navigationIcon = {
                            if (chromeState.showBackButton.value) {
                                IconButton(onClick = {
                                    chromeState.onBackClick.value?.invoke()
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (chromeState.showBottomBar.value) {
                    NavigationBar {
                        chromeState.bottomBarItems.value.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = chromeState.selectedTab.intValue == index,
                                onClick = {
                                    chromeState.selectedTab.intValue = index
                                    chromeState.onTabSelected.value(index)
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (chromeState.showFab.value) {
                    FloatingActionButton(onClick = {
                        chromeState.onFabClick.value?.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar"
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = dest,
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                        onNavigateToClient = {
                            navController.navigate("client_tabs") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToAdmin = {
                            navController.navigate("admin_tabs") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onNavigateBack = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onNavigateToClient = {
                            navController.navigate("client_tabs") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable("forgot_password") {
                    PasswordRecoveryScreen(
                        onNavigateBack = {
                            navController.navigate("login") {
                                popUpTo("forgot_password") { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    "reset_password/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    ResetPasswordScreen(
                        email = email,
                        onNavigateBack = { navController.popBackStack() },
                        onResetSuccess = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable("client_tabs") {
                    ClientHomeScreen(
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        chromeState = chromeState
                    )
                }

                composable("admin_tabs") {
                    AdminHomeScreen(
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        chromeState = chromeState
                    )
                }
            }
        }
    }
}
