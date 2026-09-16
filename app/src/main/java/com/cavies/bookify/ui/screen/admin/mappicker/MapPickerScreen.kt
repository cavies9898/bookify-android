package com.cavies.bookify.ui.screen.admin.mappicker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapPickerScreen(
    onLocationSelected: (Double, Double) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var userLat by remember { mutableStateOf(19.4326) }
    var userLng by remember { mutableStateOf(-99.1332) }
    var locationReady by remember { mutableStateOf(false) }

    if (!locationReady) {
        getCurrentLocation(context) { lat, lng ->
            userLat = lat
            userLng = lng
            locationReady = true
        }
    }

    if (locationReady) {
        AndroidView(
            factory = { ctx ->
                val mainHandler = Handler(Looper.getMainLooper())

                WebView(ctx).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.post {
                                val height = view.height
                                view.evaluateJavascript(
                                    "document.body.style.height = '${height}px'; document.getElementById('map').style.height = '${height}px';",
                                    null
                                )
                                view.evaluateJavascript(
                                    "setMapCenter($userLat, $userLng, 15);",
                                    null
                                )
                            }
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.userAgentString =
                        "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                    addJavascriptInterface(object : Any() {
                        @JavascriptInterface
                        fun onLocationSelected(lat: Double, lng: Double) {
                            mainHandler.post {
                                onLocationSelected(lat, lng)
                            }
                        }
                    }, "Android")

                    val html = ctx.assets.open("map_picker.html").bufferedReader().readText()
                    loadDataWithBaseURL(
                        "file:///android_asset/",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
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
