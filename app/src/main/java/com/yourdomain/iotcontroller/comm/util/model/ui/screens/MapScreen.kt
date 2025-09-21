package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.model.GpsLocation
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var gpsLoc by remember { mutableStateOf<GpsLocation?>(null) }
    var fetching by remember { mutableStateOf(false) }

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
        Text("GPS Location", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = gpsLoc?.let { "Lat: ${it.latitude}, Lon: ${it.longitude}" } ?: "Fetching...",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { fetchLocation() }, enabled = !fetching, modifier = Modifier.fillMaxWidth()) {
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