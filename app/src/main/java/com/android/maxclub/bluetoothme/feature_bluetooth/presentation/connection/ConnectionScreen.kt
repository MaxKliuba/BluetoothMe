package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components.BluetoothDeviceConnectedItem
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components.BluetoothDeviceConnectingItem
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components.BluetoothDeviceItem
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.components.EmptyListPlaceholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    onClickNavigationIcon: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state: ConnectionUiState by viewModel.uiState

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ConnectionUiEvent.OnShowMissingPermissionMessage -> {
                    Toast.makeText(context, event.permissions.joinToString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is ConnectionUiEvent.OnShowConnectionErrorMessage -> {
                    launch(Dispatchers.Main) {
                        snackbarHostState.showSnackbar(
                            message = context.getString(
                                R.string.connection_error_message,
                                event.device.name
                            ),
                            actionLabel = context.getString(R.string.reconnect_button),
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        ).let { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.onEvent(ConnectionEvent.OnConnect(event.device))
                            }
                        }
                    }
                }
                is ConnectionUiEvent.OnOpenBluetoothSettings -> {
                    val bluetoothSettingIntent = Intent().apply {
                        action = Settings.ACTION_BLUETOOTH_SETTINGS
                    }
                    context.startActivity(bluetoothSettingIntent)
                }
                is ConnectionUiEvent.OnShowDeviceType -> {
                    launch(Dispatchers.Main) {
                        snackbarHostState.showSnackbar(
                            message = event.deviceType,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
                is ConnectionUiEvent.OnConnected -> {
                    launch(Dispatchers.Main) {
                        delay(150)
                        scrollState.animateScrollToItem(0)
                    }
                }
            }
        }
    }

    val onStartScan = {
        viewModel.onEvent(ConnectionEvent.OnStartScan)
    }
    val onStopScan = {
        viewModel.onEvent(ConnectionEvent.OnStopScan)
    }
    val onShowBluetoothSettings = {
        viewModel.onEvent(ConnectionEvent.OnOpenBluetoothSettings)
    }

    val onClickIcon: (String) -> Unit = {
        viewModel.onEvent(ConnectionEvent.OnClickDeviceIcon(it))
    }
    val onConnect: (BluetoothDevice) -> Unit = {
        viewModel.onEvent(ConnectionEvent.OnConnect(it))
    }
    val onDisconnect: (BluetoothDevice) -> Unit = {
        viewModel.onEvent(ConnectionEvent.OnDisconnect(it))
    }
    val onSelectConnectionType: (BluetoothDevice, ConnectionType) -> Unit =
        { device, connectionType ->
            viewModel.onEvent(
                ConnectionEvent.OnUpdateBluetoothDevice(
                    device.copy(type = device.type.copy(connectionType = connectionType))
                )
            )
        }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.connection_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onClickNavigationIcon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_24),
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                    }
                },
                actions = {
                    if (state.bluetoothState is BluetoothState.On) {
                        if (state.isScanning) {
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
                    IconButton(onClick = onShowBluetoothSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bluetooth_settings_24),
                            contentDescription = stringResource(id = R.string.bluetooth_settings_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = state.isLoading,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.devices.isEmpty()) {
                EmptyListPlaceholder()
            }

            if (state.devices.isNotEmpty()) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        items = state.devices,
                        key = { it.address },
                    ) { device ->
                        Box(modifier = Modifier.animateItemPlacement()) {
                            when (device.state) {
                                BluetoothDeviceState.Connected -> BluetoothDeviceConnectedItem(
                                    device = device,
                                    onClickIcon = onClickIcon,
                                    onClickItem = onDisconnect,
                                )
                                BluetoothDeviceState.Connecting,
                                BluetoothDeviceState.Disconnecting -> BluetoothDeviceConnectingItem(
                                    device = device,
                                    onClickIcon = onClickIcon,
                                    onClickItem = onDisconnect,
                                )
                                else -> BluetoothDeviceItem(
                                    device = device,
                                    onClickIcon = onClickIcon,
                                    onClickItem = onConnect,
                                    onSelectConnectionType = onSelectConnectionType,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
