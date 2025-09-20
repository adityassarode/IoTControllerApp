package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.model.GpsLocation
import org.maplibre.gl.compose.MapLibreMap
import org.maplibre.gl.compose.camera.rememberCameraPositionState
import org.maplibre.gl.compose.model.LatLng
import org.maplibre.gl.compose.annotation.Marker

@Composable
fun MapScreen(navController: NavController) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Offline Map & GPS", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(28.dp))

        val cameraPositionState = rememberCameraPositionState {
            position = LatLng(gpsLoc?.latitude ?: 0.0, gpsLoc?.longitude ?: 0.0)
        }

        MapLibreMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            cameraPositionState = cameraPositionState,
            styleUri = "asset://style.json"
        ) {
            gpsLoc?.let { loc ->
                Marker(position = LatLng(loc.latitude, loc.longitude))
            }
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