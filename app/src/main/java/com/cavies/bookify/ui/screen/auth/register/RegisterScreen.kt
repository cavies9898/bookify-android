package com.cavies.bookify.ui.screen.auth.register

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PersonAdd
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
import com.cavies.bookify.core.domain.usecase.RegisterUseCase
import com.cavies.bookify.ui.component.ActionCardButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToClient: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    HandleRegisterEvents(viewModel, onNavigateToClient)

    val nameErrorMessage = mapNameError(uiState.nameError)
    val emailErrorMessage = mapEmailError(uiState.emailError)
    val passwordErrorMessage = mapPasswordError(uiState.passwordError)

    fun performRegister() {
        focusManager.clearFocus()
        viewModel.register(name, email, password)
    }

    RegisterContent(
        name = name,
        onNameChange = {
            name = it
            viewModel.clearNameError()
        },
        nameError = nameErrorMessage,
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
        serverError = uiState.serverError,
        isLoading = uiState.isLoading,
        onRegister = { performRegister() },
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun HandleRegisterEvents(
    viewModel: RegisterViewModel,
    onNavigateToClient: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.NavigateToClient -> onNavigateToClient()
            }
        }
    }
}

@Composable
private fun mapNameError(error: RegisterUseCase.ValidationError?): String? {
    return when (error) {
        is RegisterUseCase.ValidationError.EmptyName -> stringResource(R.string.error_empty_name)
        is RegisterUseCase.ValidationError.ShortName -> stringResource(R.string.error_short_name)
        else -> null
    }
}

@Composable
private fun mapEmailError(error: RegisterUseCase.ValidationError?): String? {
    return when (error) {
        is RegisterUseCase.ValidationError.EmptyEmail -> stringResource(R.string.error_empty_email)
        is RegisterUseCase.ValidationError.InvalidEmail -> stringResource(R.string.error_invalid_email)
        else -> null
    }
}

@Composable
private fun mapPasswordError(error: RegisterUseCase.ValidationError?): String? {
    return when (error) {
        is RegisterUseCase.ValidationError.EmptyPassword -> stringResource(R.string.error_empty_password)
        is RegisterUseCase.ValidationError.ShortPassword -> stringResource(R.string.error_short_password)
        else -> null
    }
}

@Composable
private fun RegisterContent(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
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
    onRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterHeader(onNavigateBack)
        Spacer(modifier = Modifier.height(24.dp))
        NameField(name, onNameChange, nameError)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(email, onEmailChange, emailError)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(password, onPasswordChange, passwordError, passwordVisible, onPasswordVisibilityToggle, onRegister)
        ServerError(serverError)
        Spacer(modifier = Modifier.height(24.dp))

        val isValidEmail = email.contains("@") && email.contains(".")
        val isValidPassword = password.length >= 6
        val isRegisterValid = name.length >= 2 && isValidEmail && isValidPassword

        ActionCardButton(
            icon = Icons.Default.PersonAdd,
            label = if (isLoading) stringResource(R.string.btn_register_loading) else stringResource(R.string.btn_register),
            onClick = onRegister,
            enabled = !isLoading && isRegisterValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RegisterHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
        }
    }
    Icon(
        imageVector = Icons.Default.PersonAdd,
        contentDescription = null,
        modifier = Modifier.height(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.register_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun NameField(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.label_name)) },
        isError = nameError != null,
        supportingText = nameError?.let { error ->
            { Text(error, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        modifier = Modifier.fillMaxWidth()
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
        label = { Text(stringResource(R.string.label_password_with_hint)) },
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
