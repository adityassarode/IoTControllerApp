package com.yourdomain.iotcontroller.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "IoT Controller",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { navController.navigate("call") }, modifier = Modifier.fillMaxWidth()) {
            Text("Call Device")
        }
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = { navController.navigate("sms") }, modifier = Modifier.fillMaxWidth()) {
            Text("Send SMS")
        }
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { navController.navigate("sos") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SOS", color = MaterialTheme.colorScheme.onError)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = { navController.navigate("map") }, modifier = Modifier.fillMaxWidth()) {
            Text("View Map")
        }
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = { navController.navigate("status") }, modifier = Modifier.fillMaxWidth()) {
            Text("Device Status")
        }
    }
}
