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
fun SmsScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var phoneNumber by remember { mutableStateOf("") }
    var smsText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var smsStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Send SMS", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Enter Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = smsText,
            onValueChange = { smsText = it },
            label = { Text("Message Content") },
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isSending = true
                smsStatus = "Sending..."
                coroutineScope.launch {
                    val result = ESP32WifiClient.sms(phoneNumber, smsText)
                    smsStatus = if (result) "SMS sent to device." else "Failed to send SMS."
                    NotificationUtils.showSMSNotification(
                        navController.context,
                        phoneNumber,
                        smsText.take(32)
                    )
                    isSending = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneNumber.length in 7..15 && smsText.isNotBlank() && !isSending
        ) {
            Text("Send SMS")
        }

        Spacer(modifier = Modifier.height(16.dp))

        smsStatus?.let {
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
