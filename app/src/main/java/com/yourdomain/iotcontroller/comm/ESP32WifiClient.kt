package com.yourdomain.iotcontroller.comm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

object ESP32WifiClient {
    private const val DEVICE_IP = "192.168.4.1"
    private const val DEVICE_PORT = 5050
    private const val TIMEOUT_MS = 3000

    private suspend fun sendCommand(
        command: String,
        expectResponse: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(DEVICE_IP, DEVICE_PORT), TIMEOUT_MS)
                val outputStream: OutputStream = socket.getOutputStream()
                outputStream.write(command.toByteArray())
                outputStream.flush()

                if (expectResponse) {
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    reader.readLine() ?: "NO_RESPONSE"
                } else {
                    "OK_SENT"
                }
            }
        } catch (ex: SocketTimeoutException) {
            "ERROR:_TIMEOUT"
        } catch (ex: Exception) {
            "ERROR:${ex.javaClass.simpleName}"
        }
    }

    suspend fun call(number: String): Boolean {
        val resp = sendCommand("CALL:$number\n", true)
        return resp.startsWith("OK")
    }

    suspend fun sms(number: String, text: String): Boolean {
        val resp = sendCommand("SMS:$number:$text\n", true)
        return resp.startsWith("OK")
    }

    suspend fun sos(): Boolean {
        val resp = sendCommand("SOS\n", true)
        return resp.startsWith("OK")
    }

    suspend fun cancelSos(): Boolean {
        val resp = sendCommand("SOS_CANCEL\n", true)
        return resp.startsWith("OK")
    }

    suspend fun requestGPS(): String {
        val resp = sendCommand("GPS_REQUEST\n", true)
        return if (resp.startsWith("GPS:")) resp.substringAfter("GPS:") else "No GPS data"
    }

    suspend fun deviceStatus(): String {
        val resp = sendCommand("STATUS\n", true)
        return if (resp.startsWith("STATUS:")) resp.substringAfter("STATUS:") else resp
    }
}
