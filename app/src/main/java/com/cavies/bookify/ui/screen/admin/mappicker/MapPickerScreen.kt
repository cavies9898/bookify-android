package com.cavies.bookify.ui.screen.admin.mappicker

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cavies.bookify.R
import com.cavies.bookify.ui.util.getCurrentLocation

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
        Box(modifier = Modifier.fillMaxSize()) {
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

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 8.dp)
                    .align(Alignment.TopStart),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = Color.Black
                )
            }
        }
    }
}
