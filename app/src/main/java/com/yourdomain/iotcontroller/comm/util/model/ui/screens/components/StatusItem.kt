package com.yourdomain.iotcontroller.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusItem(
    label: String,
    isActive: Boolean,
    activeColor: Color = Color(0xFF4CAF50), // Green
    inactiveColor: Color = Color(0xFFF44336) // Red
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = if (isActive) activeColor.copy(alpha = 0.1f)
                        else inactiveColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (isActive) "Online" else "Offline",
            color = if (isActive) activeColor else inactiveColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}
