package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R

@Composable
fun EmptyListPlaceholder(
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_list_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.empty_list_message),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onStartScan) {
            Text(text = stringResource(id = R.string.start_scan_button))
        }
    }
}