package com.yourdomain.iotcontroller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as? Array<*>
                pdus?.forEach { pdu ->
                    val format = bundle.getString("format")
                    val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    sms.forEach { message ->
                        val sender = message.displayOriginatingAddress
                        val content = message.displayMessageBody
                        Log.d("SmsBroadcastReceiver", "SMS received from $sender: $content")
                        Toast.makeText(
                            context,
                            "SMS from $sender: $content",
                            Toast.LENGTH_LONG
                        ).show()
                        // You may trigger a local notification or an event here
                    }
                }
            }
        }
    }
}
