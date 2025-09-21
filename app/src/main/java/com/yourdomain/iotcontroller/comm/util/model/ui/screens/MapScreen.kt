package com.yourdomain.iotcontroller.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.model.GpsLocation
import org.maplibre.maps.MapView
import org.maplibre.maps.MapLibre
import org.maplibre.maps.Style
import org.maplibre.maps.camera.CameraPosition
import org.maplibre.maps.geometry.LatLng
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var gpsLoc by remember { mutableStateOf<GpsLocation?>(null) }
    var fetching by remember { mutableStateOf(false) }

    // Fetch GPS location from ESP32
    fun fetchLocation() {
        fetching = true
        coroutineScope.launch {
            val rawLocation = ESP32WifiClient.requestGPS()
            gpsLoc = GpsLocation.parse(rawLocation)
            fetching = false
        }
    }

    LaunchedEffect(Unit) { fetchLocation() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Offline Map & GPS", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(28.dp))

        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AndroidView(factory = { ctx: Context ->
                MapView(ctx).apply {
                    getMapAsync { map ->
                        map.setStyle(Style.MAPBOX_STREETS)
                        gpsLoc?.let {
                            map.cameraPosition = CameraPosition.Builder()
                                .target(LatLng(it.latitude, it.longitude))
                                .zoom(15.0)
                                .build()
                        }
                    }
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { fetchLocation() },
            enabled = !fetching,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (!fetching) "Refresh Location" else "Refreshing…")
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}