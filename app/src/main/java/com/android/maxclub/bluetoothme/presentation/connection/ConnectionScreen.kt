package com.android.maxclub.bluetoothme.presentation.connection

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDeviceState
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import com.android.maxclub.bluetoothme.presentation.connection.components.BluetoothDeviceConnectedItem
import com.android.maxclub.bluetoothme.presentation.connection.components.BluetoothDeviceConnectingItem
import com.android.maxclub.bluetoothme.presentation.connection.components.BluetoothDeviceItem

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    navController: NavController,
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
                    snackbarHostState.showSnackbar(
                        message = event.device.name,
                        actionLabel = context.getString(R.string.reconnect_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(ConnectionEvent.OnConnect(event.device))
                        }
                    }
                }
                is ConnectionUiEvent.OnShowBluetoothSettings -> {
                    val bluetoothSettingIntent = Intent().apply {
                        action = Settings.ACTION_BLUETOOTH_SETTINGS
                    }
                    context.startActivity(bluetoothSettingIntent)
                }
                is ConnectionUiEvent.OnShowDeviceType -> {
                    snackbarHostState.showSnackbar(
                        message = event.deviceType,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }
                is ConnectionUiEvent.OnConnected -> scrollState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.connection_screen_title),
//                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_24),
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                    }
                },
                actions = {
                    if (state.bluetoothState is BluetoothState.TurnOn) {
                        if (state.isScanning) {
                            IconButton(onClick = { viewModel.onEvent(ConnectionEvent.OnStopScan) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_stop_scan_24),
                                    contentDescription = stringResource(id = R.string.stop_scan_button)
                                )
                            }
                        } else {
                            IconButton(onClick = { viewModel.onEvent(ConnectionEvent.OnStartScan) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_start_scan_24),
                                    contentDescription = stringResource(id = R.string.start_scan_button)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.onEvent(ConnectionEvent.OnClickBluetoothSettings) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bluetooth_settings_24),
                            contentDescription = stringResource(id = R.string.bluetooth_settings_button)
                        )
                    }
                },
//                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
//            { snackbarData ->
//                Snackbar(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//                    actionColor = MaterialTheme.colorScheme.primary,
//                    snackbarData = snackbarData,
//                )
//            }
        },
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                AnimatedVisibility(visible = state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                AnimatedVisibility(visible = state.devices.isEmpty()) {
                    Text(text = "Empty")
                }

                AnimatedVisibility(visible = state.devices.isNotEmpty()) {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = state.devices,
                            key = { it.address },
                        ) { device ->
                            when (device.state) {
                                BluetoothDeviceState.Connected -> BluetoothDeviceConnectedItem(
                                    device = device,
                                    onClickIcon = {
                                        viewModel.onEvent(ConnectionEvent.OnClickDeviceIcon(it))
                                    },
                                    onClickItem = {
                                        viewModel.onEvent(ConnectionEvent.OnDisconnect(it))
                                    },
                                    modifier = Modifier.animateItemPlacement(),
                                )
                                BluetoothDeviceState.Connecting,
                                BluetoothDeviceState.Disconnecting -> BluetoothDeviceConnectingItem(
                                    device = device,
                                    onClickIcon = {
                                        viewModel.onEvent(ConnectionEvent.OnClickDeviceIcon(it))
                                    },
                                    onClickItem = {
                                        viewModel.onEvent(ConnectionEvent.OnDisconnect(it))
                                    },
                                    modifier = Modifier.animateItemPlacement(),
                                )
                                else -> BluetoothDeviceItem(
                                    device = device,
                                    onClickIcon = {
                                        viewModel.onEvent(ConnectionEvent.OnClickDeviceIcon(it))
                                    },
                                    onSelectConnectionType = {
                                        viewModel.onEvent(
                                            ConnectionEvent.OnUpdateBluetoothDevice(
                                                device.copy(type = device.type.copy(connectionType = it))
                                            )
                                        )
                                    },
                                    onClickItem = { viewModel.onEvent(ConnectionEvent.OnConnect(it)) },
                                    modifier = Modifier.animateItemPlacement(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
