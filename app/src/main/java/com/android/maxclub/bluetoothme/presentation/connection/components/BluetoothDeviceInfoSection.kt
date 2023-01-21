package com.android.maxclub.bluetoothme.presentation.connection.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BluetoothDeviceInfoSection(
    deviceName: String,
    deviceAddress: String,
    connectionType: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = deviceName,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$deviceAddress${connectionType?.let { " | $it" } ?: ""}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}