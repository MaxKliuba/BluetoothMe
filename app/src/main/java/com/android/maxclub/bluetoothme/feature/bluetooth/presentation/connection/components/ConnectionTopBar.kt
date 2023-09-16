package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionTopBar(
    isAdapterEnabled: Boolean,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onOpenNavigationDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeTopAppBar(
        title = { Text(text = stringResource(id = R.string.connection_screen_title)) },
        navigationIcon = {
            IconButton(onClick = onOpenNavigationDrawer) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_24),
                    contentDescription = stringResource(R.string.menu_button)
                )
            }
        },
        actions = {
            if (isAdapterEnabled) {
                if (isScanning) {
                    IconButton(onClick = onStopScan) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stop_scan_24),
                            contentDescription = stringResource(id = R.string.stop_scan_button)
                        )
                    }
                } else {
                    IconButton(onClick = onStartScan) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_start_scan_24),
                            contentDescription = stringResource(id = R.string.start_scan_button)
                        )
                    }
                }
            }
            IconButton(onClick = onOpenBluetoothSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.bluetooth_settings_button)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}