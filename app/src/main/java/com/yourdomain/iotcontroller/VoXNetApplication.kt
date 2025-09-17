package com.yourdomain.iotcontroller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class VoXNetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // General notification channel
            val generalChannel = NotificationChannel(
                "general_channel",
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General information and status notifications"
            }

            // Emergency/SOS notification channel
            val sosChannel = NotificationChannel(
                "sos_channel",
                "SOS & Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used for SOS, alarm, and critical emergency alerts"
            }

            // Call/SMS notification channel
            val commChannel = NotificationChannel(
                "comm_channel",
                "Call and SMS Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Used for incoming and outgoing call and SMS actions"
            }

            notificationManager.createNotificationChannel(generalChannel)
            notificationManager.createNotificationChannel(sosChannel)
            notificationManager.createNotificationChannel(commChannel)
        }
    }
}
