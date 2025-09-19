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
import io.github.ramani.maps.compose.MapLibreMap
import io.github.ramani.maps.compose.model.CameraPosition
import io.github.ramani.maps.compose.model.LatLng

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

        if (gpsLoc != null) {
            MapLibreMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                style = "asset://style.json", // style.json in assets folder
                cameraPosition = CameraPosition(
                    center = LatLng(gpsLoc!!.latitude, gpsLoc!!.longitude),
                    zoom = 15.0
                ),
                markers = listOf(LatLng(gpsLoc!!.latitude, gpsLoc!!.longitude))
            )
        } else {
            Text(
                "No location yet. Press refresh to retry.",
                style = MaterialTheme.typography.titleMedium
            )
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