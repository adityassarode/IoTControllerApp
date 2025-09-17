package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.util.NotificationUtils

@Composable
fun CallScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var phoneNumber by remember { mutableStateOf("") }
    var isCalling by remember { mutableStateOf(false) }
    var callStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Place a Call", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Enter Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isCalling = true
                callStatus = "Dialing..."
                coroutineScope.launch {
                    val result = ESP32WifiClient.call(phoneNumber)
                    callStatus = if (result) "Call sent to device." else "Failed to initiate call."
                    NotificationUtils.showCallNotification(
                        navController.context,
                        phoneNumber,
                        callStatus ?: ""
                    )
                    isCalling = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneNumber.length in 7..15 && !isCalling
        ) {
            Text("Call Device")
        }

        Spacer(modifier = Modifier.height(16.dp))

        callStatus?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.contains("Failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
