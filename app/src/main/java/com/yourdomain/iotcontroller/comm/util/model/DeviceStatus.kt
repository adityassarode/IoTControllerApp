package com.yourdomain.iotcontroller.model

data class DeviceStatus(
    val deviceAOnline: Boolean,
    val deviceBOnline: Boolean,
    val gsmOnline: Boolean,
    val lastSosActive: Boolean,
    val lastGps: String,
    val lastError: String? = null
) {
    companion object {
        // Parse from status string reply, e.g. "STATUS:DEVICE_A:1,DEVICE_B:1,GSM:1,SOS:0,GPS:19.0,73.2,ERROR:"
        fun parse(raw: String): DeviceStatus {
            val fields = raw.removePrefix("STATUS:").split(',')
            var deviceAOnline = false
            var deviceBOnline = false
            var gsmOnline = false
            var lastSosActive = false
            var lastGps = ""
            var lastError: String? = null
            for (f in fields) {
                if (f.startsWith("DEVICE_A:")) deviceAOnline = f.endsWith("1")
                else if (f.startsWith("DEVICE_B:")) deviceBOnline = f.endsWith("1")
                else if (f.startsWith("GSM:")) gsmOnline = f.endsWith("1")
                else if (f.startsWith("SOS:")) lastSosActive = f.endsWith("1")
                else if (f.startsWith("GPS:")) lastGps = f.removePrefix("GPS:")
                else if (f.startsWith("ERROR:")) lastError = f.removePrefix("ERROR:").takeIf { it.isNotBlank() }
            }
            return DeviceStatus(
                deviceAOnline = deviceAOnline,
                deviceBOnline = deviceBOnline,
                gsmOnline = gsmOnline,
                lastSosActive = lastSosActive,
                lastGps = lastGps,
                lastError = lastError
            )
        }
    }
}
