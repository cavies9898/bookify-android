package com.cavies.bookify.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cavies.bookify.R
import com.cavies.bookify.core.domain.repository.AuthRepository
import com.cavies.bookify.ui.component.AppHeader
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
                user != null && user.role.name == "ADMIN" -> Routes.ADMIN_TABS
                user != null -> Routes.CLIENT_TABS
                else -> Routes.LOGIN
            }
        }
    }
}

@Composable
fun BookifyNavHost(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination by viewModel.startDestination.collectAsState()
    val chromeState = rememberAppChromeState()
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val useHeader = chromeState.useAppHeader.value

    SideEffect {
        val window = (view.context as android.app.Activity).window
        if (useHeader) {
            window.statusBarColor = primaryColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        } else {
            window.statusBarColor = backgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    startDestination?.let { dest ->
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (chromeState.useAppHeader.value) {
                    AppHeader(
                        title = chromeState.title.value,
                        onBack = chromeState.onBackClick.value
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
                            contentDescription = stringResource(R.string.cd_add)
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
                composable(Routes.LOGIN) {
                    LaunchedEffect(Unit) {
                        chromeState.title.value = "Bookify"
                        chromeState.useAppHeader.value = false
                        chromeState.showBackButton.value = false
                        chromeState.onBackClick.value = null
                        chromeState.showBottomBar.value = false
                        chromeState.showFab.value = false
                    }
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                        onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                        onNavigateToClient = {
                            navController.navigate(Routes.CLIENT_TABS) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToAdmin = {
                            navController.navigate(Routes.ADMIN_TABS) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.REGISTER) {
                    LaunchedEffect(Unit) {
                        chromeState.title.value = "Registrarse"
                        chromeState.useAppHeader.value = false
                        chromeState.showBackButton.value = true
                        chromeState.onBackClick.value = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) { inclusive = true }
                            }
                        }
                        chromeState.showBottomBar.value = false
                        chromeState.showFab.value = false
                    }
                    RegisterScreen(
                        onNavigateToClient = {
                            navController.navigate(Routes.CLIENT_TABS) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.FORGOT_PASSWORD) {
                    LaunchedEffect(Unit) {
                        chromeState.title.value = "Recuperar Contraseña"
                        chromeState.useAppHeader.value = false
                        chromeState.showBackButton.value = true
                        chromeState.onBackClick.value = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                            }
                        }
                        chromeState.showBottomBar.value = false
                        chromeState.showFab.value = false
                    }
                    PasswordRecoveryScreen(
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    Routes.RESET_PASSWORD,
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    LaunchedEffect(Unit) {
                        chromeState.title.value = "Restablecer Contraseña"
                        chromeState.useAppHeader.value = false
                        chromeState.showBackButton.value = true
                        chromeState.onBackClick.value = { navController.popBackStack() }
                        chromeState.showBottomBar.value = false
                        chromeState.showFab.value = false
                    }
                    ResetPasswordScreen(
                        email = email,
                        onResetSuccess = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.CLIENT_TABS) {
                    ClientHomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        chromeState = chromeState
                    )
                }

                composable(Routes.ADMIN_TABS) {
                    AdminHomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
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
