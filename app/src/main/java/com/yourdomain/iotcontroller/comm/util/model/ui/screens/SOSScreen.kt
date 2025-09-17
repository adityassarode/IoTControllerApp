package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.util.NotificationUtils

@Composable
fun SOSScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var sosActive by remember { mutableStateOf(false) }
    var sosStatus by remember { mutableStateOf<String?>(null) }
    var isWorking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (sosActive) Color.Red else MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (sosActive) "SOS ACTIVE" else "SOS Emergency",
            style = MaterialTheme.typography.headlineLarge,
            color = if (sosActive) Color.White else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isWorking = true
                sosStatus = "Sending SOS..."
                coroutineScope.launch {
                    val success = ESP32WifiClient.sos()
                    sosActive = success
                    sosStatus = if (success) "SOS sent and is active!" else "Failed to trigger SOS."
                    if (success) NotificationUtils.showSOSNotification(navController.context)
                    isWorking = false
                }
            },
            enabled = !isWorking && !sosActive,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger SOS", color = Color.White)
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (sosActive) {
            Button(
                onClick = {
                    isWorking = true
                    sosStatus = "Cancelling SOS..."
                    coroutineScope.launch {
                        val success = ESP32WifiClient.cancelSos()
                        sosActive = !success
                        sosStatus =
                            if (success) "SOS cancelled." else "Failed to cancel SOS."
                        if (success) NotificationUtils.cancelSOSNotification(navController.context)
                        isWorking = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel SOS", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        sosStatus?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.contains("Failed")) Color.Yellow else Color.White
            )
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
