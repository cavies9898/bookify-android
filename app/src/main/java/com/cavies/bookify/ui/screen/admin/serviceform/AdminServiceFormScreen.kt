package com.cavies.bookify.ui.screen.admin.serviceform

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavies.bookify.R
import com.cavies.bookify.ui.component.ActionCardButton
import com.cavies.bookify.ui.component.LocationMapCard
import com.cavies.bookify.ui.component.LoadingOverlay
import com.cavies.bookify.ui.util.getCurrentLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceFormScreen(
    serviceId: Long?,
    navController: androidx.navigation.NavController,
    viewModel: AdminServiceFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(serviceId) {
        serviceId?.let { viewModel.loadService(it) }
    }

    val currentBackStackEntry = navController.currentBackStackEntry
    val mapResultLat = currentBackStackEntry?.savedStateHandle?.get<String>("map_result_lat")
    val mapResultLng = currentBackStackEntry?.savedStateHandle?.get<String>("map_result_lng")
    LaunchedEffect(mapResultLat, mapResultLng) {
        if (!mapResultLat.isNullOrBlank()) viewModel.updateLatitude(mapResultLat)
        if (!mapResultLng.isNullOrBlank()) viewModel.updateLongitude(mapResultLng)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateLatitude("%.6f".format(lat))
                viewModel.updateLongitude("%.6f".format(lng))
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ServiceBasicFields(
                name = uiState.name,
                description = uiState.description,
                onNameChange = viewModel::updateName,
                onDescriptionChange = viewModel::updateDescription
            )
            Spacer(modifier = Modifier.height(12.dp))

            ServiceNumericField(
                label = stringResource(R.string.service_form_label_duration),
                value = uiState.durationMinutes,
                range = 15..480,
                step = 15,
                onValueChange = viewModel::updateDurationMinutes
            )
            Spacer(modifier = Modifier.height(12.dp))

            ServiceNumericField(
                label = stringResource(R.string.service_form_label_capacity),
                value = uiState.capacity,
                range = 1..100,
                step = 1,
                onValueChange = viewModel::updateCapacity
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.price,
                onValueChange = { viewModel.updatePrice(it) },
                label = { Text(stringResource(R.string.service_form_label_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ServiceTimeFields(
                openingTime = uiState.openingTime,
                closingTime = uiState.closingTime,
                onOpeningTimeChange = viewModel::updateOpeningTime,
                onClosingTimeChange = viewModel::updateClosingTime
            )
            Spacer(modifier = Modifier.height(12.dp))

            ServiceLocationFields(
                address = uiState.location,
                latitude = uiState.latitude,
                longitude = uiState.longitude,
                hasCoordinates = uiState.latitude.isNotBlank() && uiState.longitude.isNotBlank(),
                onAddressChange = viewModel::updateLocation,
                onLatitudeChange = viewModel::updateLatitude,
                onLongitudeChange = viewModel::updateLongitude,
                onMapClick = { navController.navigate("admin_map_picker") },
                onCurrentLocationClick = {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse) {
                        getCurrentLocation(context) { lat, lng ->
                            viewModel.updateLatitude("%.6f".format(lat))
                            viewModel.updateLongitude("%.6f".format(lng))
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ActionCardButton(
                icon = Icons.Default.Save,
                label = if (uiState.isLoading) {
                    stringResource(R.string.service_form_btn_saving)
                } else {
                    stringResource(R.string.service_form_btn_save)
                },
                onClick = {
                    viewModel.saveService(serviceId) {
                        navController.popBackStack()
                    }
                },
                enabled = !uiState.isLoading && uiState.name.isNotBlank()
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun ServiceBasicFields(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.service_form_label_name)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text(stringResource(R.string.service_form_label_description)) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun ServiceNumericField(
    label: String,
    value: Int,
    range: IntRange,
    step: Int,
    onValueChange: (Int) -> Unit
) {
    Text(label, style = MaterialTheme.typography.bodyMedium)
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            if (value > range.first) onValueChange(value - step)
        }) {
            Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.service_form_cd_decrease))
        }
        Text("$value", style = MaterialTheme.typography.bodyLarge)
        IconButton(onClick = {
            if (value < range.last) onValueChange(value + step)
        }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.service_form_cd_increase))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceTimeFields(
    openingTime: String,
    closingTime: String,
    onOpeningTimeChange: (String) -> Unit,
    onClosingTimeChange: (String) -> Unit
) {
    var showOpeningTimePicker by remember { mutableStateOf(false) }
    var showClosingTimePicker by remember { mutableStateOf(false) }

    val openingParts = openingTime.split(":")
    val closingParts = closingTime.split(":")
    var openingHour by remember { mutableIntStateOf(openingParts.getOrNull(0)?.toIntOrNull() ?: 9) }
    var openingMinute by remember { mutableIntStateOf(openingParts.getOrNull(1)?.toIntOrNull() ?: 0) }
    var closingHour by remember { mutableIntStateOf(closingParts.getOrNull(0)?.toIntOrNull() ?: 17) }
    var closingMinute by remember { mutableIntStateOf(closingParts.getOrNull(1)?.toIntOrNull() ?: 0) }

    LaunchedEffect(openingTime) {
        val p = openingTime.split(":")
        openingHour = p.getOrNull(0)?.toIntOrNull() ?: 9
        openingMinute = p.getOrNull(1)?.toIntOrNull() ?: 0
    }
    LaunchedEffect(closingTime) {
        val p = closingTime.split(":")
        closingHour = p.getOrNull(0)?.toIntOrNull() ?: 17
        closingMinute = p.getOrNull(1)?.toIntOrNull() ?: 0
    }

    if (showOpeningTimePicker) {
        ListTimePickerDialog(
            hour = openingHour,
            minute = openingMinute,
            onHourChange = { openingHour = it },
            onMinuteChange = { openingMinute = it },
            onConfirm = {
                onOpeningTimeChange("%02d:%02d".format(openingHour, openingMinute))
                showOpeningTimePicker = false
            },
            onDismiss = { showOpeningTimePicker = false }
        )
    }

    if (showClosingTimePicker) {
        ListTimePickerDialog(
            hour = closingHour,
            minute = closingMinute,
            onHourChange = { closingHour = it },
            onMinuteChange = { closingMinute = it },
            onConfirm = {
                onClosingTimeChange("%02d:%02d".format(closingHour, closingMinute))
                showClosingTimePicker = false
            },
            onDismiss = { showClosingTimePicker = false }
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = openingTime,
            onValueChange = {},
            label = { Text(stringResource(R.string.service_form_label_opening_time)) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showOpeningTimePicker = true }
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = closingTime,
            onValueChange = {},
            label = { Text(stringResource(R.string.service_form_label_closing_time)) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showClosingTimePicker = true }
        )
    }
}

@Composable
private fun ServiceLocationFields(
    address: String,
    latitude: String,
    longitude: String,
    hasCoordinates: Boolean,
    onAddressChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onMapClick: () -> Unit,
    onCurrentLocationClick: () -> Unit
) {
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text(stringResource(R.string.service_form_label_address)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = latitude,
            onValueChange = onLatitudeChange,
            label = { Text(stringResource(R.string.service_form_label_latitude)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedTextField(
            value = longitude,
            onValueChange = onLongitudeChange,
            label = { Text(stringResource(R.string.service_form_label_longitude)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    ActionCardButton(
        icon = Icons.Default.MyLocation,
        label = stringResource(R.string.service_form_btn_map),
        onClick = onMapClick
    )
    Spacer(modifier = Modifier.height(12.dp))

    ActionCardButton(
        icon = Icons.Default.Place,
        label = stringResource(R.string.service_form_btn_current_location),
        onClick = onCurrentLocationClick
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (hasCoordinates) {
        key(latitude, longitude) {
            LocationMapCard(
                location = address.ifBlank { stringResource(R.string.service_form_selected_location) },
                latitude = latitude.toDoubleOrNull(),
                longitude = longitude.toDoubleOrNull()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListTimePickerDialog(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.service_form_time_picker_title)) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onHourChange(if (hour > 0) hour - 1 else 23) }) {
                        Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.service_form_time_picker_cd_hour_less))
                    }
                    Text(
                        text = "%02d".format(hour),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { onHourChange(if (hour < 23) hour + 1 else 0) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.service_form_time_picker_cd_hour_more))
                    }
                    Text(stringResource(R.string.service_form_time_picker_hour), style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onMinuteChange(if (minute > 0) minute - 1 else 59) }) {
                        Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.service_form_time_picker_cd_minute_less))
                    }
                    Text(
                        text = "%02d".format(minute),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { onMinuteChange(if (minute < 59) minute + 1 else 0) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.service_form_time_picker_cd_minute_more))
                    }
                    Text(stringResource(R.string.service_form_time_picker_minute), style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.service_form_time_picker_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.service_form_time_picker_cancel)) }
        }
    )
}
