package com.yourdomain.iotcontroller.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.yourdomain.iotcontroller.comm.ESP32WifiClient
import com.yourdomain.iotcontroller.comm.AudioStreamer
import com.yourdomain.iotcontroller.comm.AudioReceiver
import com.yourdomain.iotcontroller.util.NotificationUtils

@Composable
fun CallScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var isCalling by remember { mutableStateOf(false) }
    var inCall by remember { mutableStateOf(false) }
    var useSpeaker by remember { mutableStateOf(false) }
    var callStatus by remember { mutableStateOf<String?>(null) }
    var audioReceiver by remember { mutableStateOf<AudioReceiver?>(null) }

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
            modifier = Modifier.fillMaxWidth(),
            enabled = !inCall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isCalling = true
                callStatus = "Dialing..."
                coroutineScope.launch {
                    val result = ESP32WifiClient.call(phoneNumber)
                    callStatus = if (result) "Call sent to device." else "Failed to initiate call."
                    if (result) {
                        inCall = true
                        // Start audio streaming both ways
                        AudioStreamer.startStreamingAudio()
                        audioReceiver = AudioReceiver(
                            context,
                            useSpeaker = useSpeaker
                        ).apply { start() }
                    } else {
                        inCall = false
                    }
                    NotificationUtils.showCallNotification(
                        context,
                        phoneNumber,
                        callStatus ?: ""
                    )
                    isCalling = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneNumber.length in 7..15 && !isCalling && !inCall
        ) {
            Text("Call Device")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (inCall) {
            Button(
                onClick = {
                    // End Call
                    inCall = false
                    callStatus = "Call ended."
                    AudioStreamer.stopStreamingAudio()
                    audioReceiver?.stop()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("End Call")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    useSpeaker = !useSpeaker
                    audioReceiver?.stop()
                    audioReceiver = AudioReceiver(
                        context,
                        useSpeaker = useSpeaker
                    ).apply { start() }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
            ) {
                Text(if (useSpeaker) "Switch to Earpiece" else "Switch to Speaker")
            }
        }

        callStatus?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.contains("Failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
