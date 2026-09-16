package com.cavies.bookify.ui.screen.admin.serviceform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavies.bookify.ui.component.ActionCardButton
import com.cavies.bookify.ui.component.LocationMapCard
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceFormScreen(
    serviceId: Long?,
    navController: androidx.navigation.NavController,
    viewModel: AdminServiceFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showOpeningTimePicker by remember { mutableStateOf(false) }
    var showClosingTimePicker by remember { mutableStateOf(false) }

    val openingParts = uiState.openingTime.split(":")
    val closingParts = uiState.closingTime.split(":")
    var openingHour by remember { mutableIntStateOf(openingParts.getOrNull(0)?.toIntOrNull() ?: 9) }
    var openingMinute by remember { mutableIntStateOf(openingParts.getOrNull(1)?.toIntOrNull() ?: 0) }
    var closingHour by remember { mutableIntStateOf(closingParts.getOrNull(0)?.toIntOrNull() ?: 17) }
    var closingMinute by remember { mutableIntStateOf(closingParts.getOrNull(1)?.toIntOrNull() ?: 0) }

    LaunchedEffect(uiState.openingTime) {
        val p = uiState.openingTime.split(":")
        openingHour = p.getOrNull(0)?.toIntOrNull() ?: 9
        openingMinute = p.getOrNull(1)?.toIntOrNull() ?: 0
    }
    LaunchedEffect(uiState.closingTime) {
        val p = uiState.closingTime.split(":")
        closingHour = p.getOrNull(0)?.toIntOrNull() ?: 17
        closingMinute = p.getOrNull(1)?.toIntOrNull() ?: 0
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Duración (minutos)", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (uiState.durationMinutes > 15) viewModel.updateDurationMinutes(uiState.durationMinutes - 15) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Reducir")
                }
                Text("${uiState.durationMinutes}", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { if (uiState.durationMinutes < 480) viewModel.updateDurationMinutes(uiState.durationMinutes + 15) }) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Capacidad", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (uiState.capacity > 1) viewModel.updateCapacity(uiState.capacity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Reducir")
                }
                Text("${uiState.capacity}", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { if (uiState.capacity < 100) viewModel.updateCapacity(uiState.capacity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.price,
                onValueChange = { viewModel.updatePrice(it) },
                label = { Text("Precio (MXN)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (showOpeningTimePicker) {
                ListTimePickerDialog(
                    hour = openingHour,
                    minute = openingMinute,
                    onHourChange = { openingHour = it },
                    onMinuteChange = { openingMinute = it },
                    onConfirm = {
                        viewModel.updateOpeningTime("%02d:%02d".format(openingHour, openingMinute))
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
                        viewModel.updateClosingTime("%02d:%02d".format(closingHour, closingMinute))
                        showClosingTimePicker = false
                    },
                    onDismiss = { showClosingTimePicker = false }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.openingTime,
                    onValueChange = {},
                    label = { Text("Hora apertura (HH:mm)") },
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
                    value = uiState.closingTime,
                    onValueChange = {},
                    label = { Text("Hora cierre (HH:mm)") },
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
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.location,
                onValueChange = { viewModel.updateLocation(it) },
                label = { Text("Dirección del negocio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.latitude,
                    onValueChange = { viewModel.updateLatitude(it) },
                    label = { Text("Latitud") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = uiState.longitude,
                    onValueChange = { viewModel.updateLongitude(it) },
                    label = { Text("Longitud") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            ActionCardButton(
                icon = Icons.Default.MyLocation,
                label = "Seleccionar en mapa",
                onClick = { navController.navigate("admin_map_picker") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ActionCardButton(
                icon = Icons.Default.Place,
                label = "Usar mi ubicación actual",
                onClick = {
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

            if (uiState.latitude.isNotBlank() && uiState.longitude.isNotBlank()) {
                key(uiState.latitude, uiState.longitude) {
                    LocationMapCard(
                        location = uiState.location.ifBlank { "Ubicación seleccionada" },
                        latitude = uiState.latitude.toDoubleOrNull(),
                        longitude = uiState.longitude.toDoubleOrNull()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ActionCardButton(
                icon = Icons.Default.Save,
                label = if (uiState.isLoading) "Guardando..." else "Guardar servicio",
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
}

private fun getCurrentLocation(context: Context, onLocation: (Double, Double) -> Unit) {
    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasFine && !hasCoarse) return

    try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocation(location.latitude, location.longitude)
                return@addOnSuccessListener
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                .setMaxUpdates(1)
                .build()

            fusedClient.requestLocationUpdates(
                request,
                object : LocationCallback() {
                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                        result.lastLocation?.let { onLocation(it.latitude, it.longitude) }
                    }
                },
                Looper.getMainLooper()
            )
        }
    } catch (_: SecurityException) {
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
        title = { Text("Seleccionar hora") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onHourChange(if (hour > 0) hour - 1 else 23) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Hora menos")
                    }
                    Text(
                        text = "%02d".format(hour),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { onHourChange(if (hour < 23) hour + 1 else 0) }) {
                        Icon(Icons.Default.Add, contentDescription = "Hora más")
                    }
                    Text("Hora", style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onMinuteChange(if (minute > 0) minute - 1 else 59) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Minuto menos")
                    }
                    Text(
                        text = "%02d".format(minute),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { onMinuteChange(if (minute < 59) minute + 1 else 0) }) {
                        Icon(Icons.Default.Add, contentDescription = "Minuto más")
                    }
                    Text("Minuto", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
