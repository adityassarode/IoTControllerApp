package com.yourdomain.iotcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourdomain.iotcontroller.ui.screens.*
import com.yourdomain.iotcontroller.ui.theme.IoTControllerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IoTControllerAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IoTControllerNavHost()
                }
            }
        }
    }
}

@Composable
fun IoTControllerNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("call") { CallScreen(navController) }
        composable("sms") { SmsScreen(navController) }
        composable("sos") { SOSScreen(navController) }
        composable("map") { MapScreen(navController) }
        composable("status") { StatusScreen(navController) }
    }
}
