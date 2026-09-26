package com.cavies.bookify.ui.screen.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavies.bookify.R
import com.cavies.bookify.core.domain.model.UserRole
import com.cavies.bookify.core.domain.usecase.LoginUseCase
import com.cavies.bookify.ui.component.ActionCardButton

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToClient: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    HandleLoginEvents(viewModel, onNavigateToClient, onNavigateToAdmin)

    val emailErrorMessage = mapEmailError(uiState.emailError)
    val passwordErrorMessage = mapPasswordError(uiState.passwordError)

    fun performLogin() {
        focusManager.clearFocus()
        viewModel.login(email, password)
    }

    LoginContent(
        email = email,
        onEmailChange = {
            email = it
            viewModel.clearEmailError()
        },
        emailError = emailErrorMessage,
        password = password,
        onPasswordChange = {
            password = it
            viewModel.clearPasswordError()
        },
        passwordError = passwordErrorMessage,
        passwordVisible = passwordVisible,
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        serverError = uiState.error,
        isLoading = uiState.isLoading,
        onLogin = { performLogin() },
        onForgotPassword = onNavigateToForgotPassword,
        onRegister = onNavigateToRegister
    )
}

@Composable
private fun HandleLoginEvents(
    viewModel: LoginViewModel,
    onNavigateToClient: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateTo -> {
                    when (event.role) {
                        UserRole.ADMIN -> onNavigateToAdmin()
                        UserRole.CLIENTE -> onNavigateToClient()
                    }
                }
            }
        }
    }
}

@Composable
private fun mapEmailError(error: LoginUseCase.ValidationError?): String? {
    return when (error) {
        is LoginUseCase.ValidationError.EmptyEmail -> stringResource(R.string.error_empty_email)
        is LoginUseCase.ValidationError.InvalidEmail -> stringResource(R.string.error_invalid_email)
        else -> null
    }
}

@Composable
private fun mapPasswordError(error: LoginUseCase.ValidationError?): String? {
    return when (error) {
        is LoginUseCase.ValidationError.EmptyPassword -> stringResource(R.string.error_empty_password)
        is LoginUseCase.ValidationError.ShortPassword -> stringResource(R.string.error_short_password)
        else -> null
    }
}

@Composable
private fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    serverError: String?,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginHeader()
        Spacer(modifier = Modifier.height(24.dp))
        EmailField(email, onEmailChange, emailError)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(password, onPasswordChange, passwordError, passwordVisible, onPasswordVisibilityToggle, onLogin)
        ServerError(serverError)
        Spacer(modifier = Modifier.height(24.dp))
        LoginButton(isLoading, onLogin)
        Spacer(modifier = Modifier.height(16.dp))
        ForgotPasswordButton(onForgotPassword)
        RegisterButton(onRegister)
    }
}

@Composable
private fun LoginHeader() {
    Icon(
        imageVector = Icons.Default.CalendarMonth,
        contentDescription = null,
        modifier = Modifier.height(48.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.login_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.label_email)) },
        isError = emailError != null,
        supportingText = emailError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onDone: () -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.label_password)) },
        isError = passwordError != null,
        supportingText = passwordError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        trailingIcon = {
            PasswordVisibilityToggle(passwordVisible, onPasswordVisibilityToggle)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: () -> Unit
) {
    val description = if (visible) stringResource(R.string.cd_hide_password) else stringResource(R.string.cd_show_password)
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = description
        )
    }
}

@Composable
private fun ServerError(error: String?) {
    if (error != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    ActionCardButton(
        icon = Icons.Filled.AccountCircle,
        label = if (isLoading) stringResource(R.string.btn_login_loading) else stringResource(R.string.btn_login),
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ForgotPasswordButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(stringResource(R.string.login_forgot_password))
    }
}

@Composable
private fun RegisterButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(stringResource(R.string.login_no_account))
    }
}
