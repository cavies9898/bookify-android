package com.cavies.bookify.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

fun getCurrentLocation(context: Context, onLocation: (Double, Double) -> Unit) {
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
