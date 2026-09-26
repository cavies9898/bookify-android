package com.cavies.bookify.ui.screen.auth.passwordrecovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavies.bookify.R
import com.cavies.bookify.core.domain.usecase.ForgotPasswordUseCase
import com.cavies.bookify.core.domain.usecase.ResetPasswordUseCase
import com.cavies.bookify.ui.component.ActionCardButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: PasswordRecoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var emailInput by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    HandlePasswordRecoveryEvents(viewModel, onNavigateToLogin)

    val emailErrorMessage = mapEmailError(uiState.emailError)
    val codeErrorMessage = mapCodeError(uiState.codeError)
    val passwordErrorMessage = mapPasswordError(uiState.passwordError)
    val confirmPasswordErrorMessage = mapConfirmPasswordError(uiState.confirmPasswordError)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Header(onNavigateBack)

        when (uiState.step) {
            1 -> StepSendCode(
                emailInput = emailInput,
                onEmailChange = {
                    emailInput = it
                    viewModel.clearEmailError()
                },
                emailError = emailErrorMessage,
                isLoading = uiState.isLoading,
                serverError = uiState.serverError,
                onSendCode = {
                    focusManager.clearFocus()
                    viewModel.forgotPassword(emailInput)
                }
            )
            2 -> StepResetPassword(
                code = code,
                onCodeChange = {
                    if (it.length <= 6) code = it
                    viewModel.clearCodeError()
                },
                codeError = codeErrorMessage,
                newPassword = newPassword,
                onNewPasswordChange = {
                    newPassword = it
                    viewModel.clearPasswordErrors()
                },
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = { passwordVisible = it },
                passwordError = passwordErrorMessage,
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword = it
                    viewModel.clearPasswordErrors()
                },
                confirmPasswordVisible = confirmPasswordVisible,
                onConfirmPasswordVisibleChange = { confirmPasswordVisible = it },
                confirmPasswordError = confirmPasswordErrorMessage,
                isLoading = uiState.isLoading,
                serverError = uiState.serverError,
                onResetPassword = {
                    focusManager.clearFocus()
                    viewModel.resetPassword(code, newPassword, confirmPassword)
                }
            )
            3 -> StepSuccess(
                successMessage = uiState.successMessage,
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@Composable
private fun HandlePasswordRecoveryEvents(
    viewModel: PasswordRecoveryViewModel,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PasswordRecoveryEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }
}

@Composable
private fun mapEmailError(error: ForgotPasswordUseCase.ValidationError?): String? {
    return when (error) {
        is ForgotPasswordUseCase.ValidationError.EmptyEmail -> stringResource(R.string.error_empty_email)
        is ForgotPasswordUseCase.ValidationError.InvalidEmail -> stringResource(R.string.error_invalid_email)
        null -> null
    }
}

@Composable
private fun mapCodeError(error: ResetPasswordUseCase.ValidationError?): String? {
    return when (error) {
        is ResetPasswordUseCase.ValidationError.EmptyCode -> stringResource(R.string.error_empty_code)
        is ResetPasswordUseCase.ValidationError.InvalidCodeLength -> stringResource(R.string.error_invalid_code_length)
        else -> null
    }
}

@Composable
private fun mapPasswordError(error: ResetPasswordUseCase.ValidationError?): String? {
    return when (error) {
        is ResetPasswordUseCase.ValidationError.EmptyPassword -> stringResource(R.string.error_empty_password)
        is ResetPasswordUseCase.ValidationError.ShortPassword -> stringResource(R.string.error_short_password_8)
        else -> null
    }
}

@Composable
private fun mapConfirmPasswordError(error: ResetPasswordUseCase.ValidationError?): String? {
    return when (error) {
        is ResetPasswordUseCase.ValidationError.PasswordsDoNotMatch -> stringResource(R.string.error_passwords_do_not_match)
        else -> null
    }
}

@Composable
private fun Header(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
        }
        Text(stringResource(R.string.recovery_title), style = MaterialTheme.typography.titleLarge)
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun StepSendCode(
    emailInput: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    isLoading: Boolean,
    serverError: String?,
    onSendCode: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        modifier = Modifier.height(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.recovery_forgot_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.recovery_forgot_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = emailInput,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.label_email)) },
        isError = emailError != null,
        supportingText = emailError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSendCode() }
        ),
        modifier = Modifier.fillMaxWidth()
    )

    ServerError(serverError)

    Spacer(modifier = Modifier.height(24.dp))

    val isValidEmail = emailInput.contains("@") && emailInput.contains(".")
    ActionCardButton(
        icon = Icons.Default.Send,
        label = if (isLoading) stringResource(R.string.recovery_send_code_loading) else stringResource(R.string.recovery_send_code),
        onClick = onSendCode,
        enabled = !isLoading && isValidEmail,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StepResetPassword(
    code: String,
    onCodeChange: (String) -> Unit,
    codeError: String?,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    passwordError: String?,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    confirmPasswordError: String?,
    isLoading: Boolean,
    serverError: String?,
    onResetPassword: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        modifier = Modifier.height(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.recovery_reset_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.recovery_reset_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(24.dp))

    CodeField(code, onCodeChange, codeError)
    Spacer(modifier = Modifier.height(12.dp))
    NewPasswordField(newPassword, onNewPasswordChange, passwordVisible, onPasswordVisibleChange, passwordError)
    Spacer(modifier = Modifier.height(12.dp))
    ConfirmPasswordField(confirmPassword, onConfirmPasswordChange, confirmPasswordVisible, onConfirmPasswordVisibleChange, confirmPasswordError)

    ServerError(serverError)

    Spacer(modifier = Modifier.height(24.dp))

    val codeValid = code.length == 6
    val passwordValid = newPassword.length >= 8
    val passwordsMatch = newPassword == confirmPassword
    val isResetValid = codeValid && passwordValid && passwordsMatch

    ActionCardButton(
        icon = Icons.Default.Lock,
        label = if (isLoading) stringResource(R.string.recovery_reset_loading) else stringResource(R.string.recovery_reset_title),
        onClick = onResetPassword,
        enabled = !isLoading && isResetValid,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    codeError: String?
) {
    OutlinedTextField(
        value = code,
        onValueChange = onCodeChange,
        label = { Text(stringResource(R.string.label_verification_code)) },
        isError = codeError != null,
        supportingText = codeError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Pin, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun NewPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    passwordError: String?
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.label_new_password)) },
        isError = passwordError != null,
        supportingText = passwordError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) stringResource(R.string.cd_hide_password) else stringResource(R.string.cd_show_password)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ConfirmPasswordField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    confirmPasswordError: String?
) {
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text(stringResource(R.string.label_confirm_password)) },
        isError = confirmPasswordError != null,
        supportingText = confirmPasswordError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = { onConfirmPasswordVisibleChange(!confirmPasswordVisible) }) {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (confirmPasswordVisible) stringResource(R.string.cd_hide_password) else stringResource(R.string.cd_show_password)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth()
    )
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
private fun StepSuccess(
    successMessage: String?,
    onNavigateToLogin: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        modifier = Modifier.height(64.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.recovery_reset_success),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = successMessage ?: stringResource(R.string.recovery_reset_fallback),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(24.dp))
    ActionCardButton(
        icon = Icons.Default.Login,
        label = stringResource(R.string.btn_go_to_login),
        onClick = onNavigateToLogin,
        modifier = Modifier.fillMaxWidth()
    )
}
