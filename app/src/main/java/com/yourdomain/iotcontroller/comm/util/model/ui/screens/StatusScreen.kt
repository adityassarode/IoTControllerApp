package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.model.DeviceStatus

@Composable
fun StatusScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var lastStatus by remember { mutableStateOf<DeviceStatus?>(null) }
    var fetching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun fetchStatus() {
        fetching = true
        error = null
        coroutineScope.launch {
            try {
                val rawStatus = ESP32WifiClient.deviceStatus()
                lastStatus = DeviceStatus.parse(rawStatus)
                error = null
            } catch (e: Exception) {
                error = "Failed to fetch device status!"
            }
            fetching = false
        }
    }

    LaunchedEffect(Unit) { fetchStatus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Device Status", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(18.dp))

        lastStatus?.let { status ->
            StatusDetails(status)
        } ?: if (fetching) {
            CircularProgressIndicator()
        } else {
            Text("No status. Press refresh.", color = MaterialTheme.colorScheme.error)
        }
        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { fetchStatus() },
            enabled = !fetching,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (!fetching) "Refresh Status" else "Refreshing…")
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

// This composable renders the details of your device status
@Composable
fun StatusDetails(status: DeviceStatus) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Device A Online: ${status.deviceAOnline}", style = MaterialTheme.typography.bodyLarge)
        Text("Device B Online: ${status.deviceBOnline}", style = MaterialTheme.typography.bodyLarge)
        Text("GSM Online: ${status.gsmOnline}", style = MaterialTheme.typography.bodyLarge)
        Text("SOS Active: ${status.lastSosActive}", style = MaterialTheme.typography.bodyLarge)
        Text("Last GPS: ${status.lastGps}", style = MaterialTheme.typography.bodyLarge)
        if (!status.lastError.isNullOrBlank())
            Text("Last Error: ${status.lastError}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
    }
}
