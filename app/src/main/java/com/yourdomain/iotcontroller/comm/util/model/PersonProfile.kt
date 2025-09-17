package com.yourdomain.iotcontroller.model

data class PersonProfile(
    val name: String,
    val emergencyContact: String,
    val medicalInfo: String? = null,
    val address: String? = null,
    val notes: String? = null
) {
    companion object {
        // Optionally parse from a CSV, JSON, or a simple backend string format
        fun parse(raw: String): PersonProfile? {
            // Example: "John Doe,9876543210,Diabetic,123 Street,Needs regular insulin"
            val parts = raw.split(",")
            return if (parts.isNotEmpty()) {
                PersonProfile(
                    name = parts.getOrElse(0) { "" },
                    emergencyContact = parts.getOrElse(1) { "" },
                    medicalInfo = parts.getOrNull(2),
                    address = parts.getOrNull(3),
                    notes = parts.getOrNull(4)
                )
            } else null
        }
    }

    override fun toString(): String {
        return "$name | $emergencyContact" +
                (medicalInfo?.let { " | $it" } ?: "") +
                (address?.let { " | $it" } ?: "") +
                (notes?.let { " | $it" } ?: "")
    }
}
