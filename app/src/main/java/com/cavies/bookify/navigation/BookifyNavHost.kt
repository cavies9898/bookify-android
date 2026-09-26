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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cavies.bookify.R
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
                user != null && user.role.name == "ADMIN" -> Routes.ADMIN_TABS
                user != null -> Routes.CLIENT_TABS
                else -> Routes.LOGIN
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
                if (chromeState.showBottomBar.value || chromeState.title.value != stringResource(R.string.app_name)) {
                    TopAppBar(
                        title = { Text(chromeState.title.value) },
                        navigationIcon = {
                            if (chromeState.showBackButton.value) {
                                IconButton(onClick = {
                                    chromeState.onBackClick.value?.invoke()
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
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
                    RegisterScreen(
                        onNavigateBack = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) { inclusive = true }
                            }
                        },
                        onNavigateToClient = {
                            navController.navigate(Routes.CLIENT_TABS) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.FORGOT_PASSWORD) {
                    PasswordRecoveryScreen(
                        onNavigateBack = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                            }
                        },
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
                    ResetPasswordScreen(
                        email = email,
                        onNavigateBack = { navController.popBackStack() },
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
