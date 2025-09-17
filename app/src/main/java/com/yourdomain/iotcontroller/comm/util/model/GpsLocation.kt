package com.yourdomain.iotcontroller.model

data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        // Parses a string like "19.0760,72.8777" or "GPS:19.0760,72.8777"
        fun parse(raw: String): GpsLocation? {
            val cleaned = raw.removePrefix("GPS:").trim()
            val parts = cleaned.split(",")
            return if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lon = parts[1].toDoubleOrNull()
                if (lat != null && lon != null) {
                    GpsLocation(lat, lon)
                } else null
            } else null
        }
    }

    override fun toString(): String = "$latitude,$longitude"
}
