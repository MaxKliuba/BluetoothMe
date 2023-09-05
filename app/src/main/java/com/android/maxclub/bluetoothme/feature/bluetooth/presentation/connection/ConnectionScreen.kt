package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.*
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileDialogData
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.toBleProfileType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    onRequestMissingPermissions: (Array<String>) -> Unit,
    onEnableAdapter: () -> Unit,
    onConnect: (BluetoothDevice) -> Unit,
    onDisconnect: (BluetoothDevice?) -> Unit,
    onOpenNavigationDrawer: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel(),
) {
    val state: ConnectionUiState by viewModel.uiState

    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ConnectionUiAction.RequestMissingPermissions -> {
                    onRequestMissingPermissions(action.permissions.toList().toTypedArray())
                }

                is ConnectionUiAction.LaunchBluetoothSettingsIntent -> {
                    launchBluetoothSettingsIntent(context)
                }

                is ConnectionUiAction.ShowDeviceType -> {
                    showDeviceTypeSnackbar(
                        snackbarHostState = snackbarHostState,
                        deviceType = action.deviceType,
                    )
                }

                is ConnectionUiAction.ScrollToConnectedDevice -> {
                    launch {
                        delay(150)
                        scrollState.scrollToItem(0)
                    }
                }
            }
        }
    }

    val onStartScan: () -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnStartScan) }
    }
    val onStopScan: () -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnStopScan) }
    }
    val onShowBluetoothSettings: () -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnShowBluetoothSettings) }
    }
    val onClickIcon: (String) -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnShowDeviceType(it)) }
    }
    val onSelectConnectionType: (BluetoothDevice, ConnectionType) -> Unit = remember {
        { device, connectionType ->
            viewModel.onEvent(
                ConnectionUiEvent.OnUpdateBluetoothDevice(
                    device.copy(type = device.type.copy(connectionType = connectionType))
                )
            )
        }
    }
    val onReselectConnectionType: (BluetoothDevice, BluetoothLeProfile) -> Unit = remember {
        { device, bleProfile ->
            val data = BleProfileDialogData(
                device = device,
                selectedBleProfileType = bleProfile.toBleProfileType(),
                serviceUuid = (bleProfile as? BluetoothLeProfile.Custom)
                    ?.serviceUuid?.toString() ?: "",
                readCharacteristicUuid = (bleProfile as? BluetoothLeProfile.Custom)
                    ?.readCharacteristicUuid?.toString() ?: "",
                writeCharacteristicUuid = (bleProfile as? BluetoothLeProfile.Custom)
                    ?.writeCharacteristicUuid?.toString() ?: "",
            )
            viewModel.onEvent(ConnectionUiEvent.OnShowBleProfileDialog(data))
        }
    }
    val onChangeBleProfileData: (BleProfileDialogData) -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnChangeBleProfileData(it)) }
    }
    val onDismissBleProfileDialog: () -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnDismissBleProfileDialog) }
    }
    val onConfirmBleProfileDialog: () -> Unit = remember {
        { viewModel.onEvent(ConnectionUiEvent.OnConfirmBleProfileDialog) }
    }

    state.bleProfileDialogData?.let { bleProfileDialogData ->
        BleProfileDialog(
            data = bleProfileDialogData,
            onChangeBleProfileData = onChangeBleProfileData,
            onDismiss = onDismissBleProfileDialog,
            onConfirm = onConfirmBleProfileDialog,
        )
    }

    Scaffold(
        topBar = {
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
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.bluetooth_settings_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.bluetoothState is BluetoothState.Off) {
                EnableAdapterPlaceholder(
                    onEnableAdapter = onEnableAdapter,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (state.bluetoothState is BluetoothState.On && state.devices.isEmpty()) {
                EmptyListPlaceholder(
                    onStartScan = onStartScan,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            if (state.bluetoothState is BluetoothState.On && state.devices.isNotEmpty()) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        items = state.devices,
                        key = { it.address },
                    ) { device ->
                        when (device.state) {
                            BluetoothDeviceState.Connected -> BluetoothDeviceConnectedItem(
                                device = device,
                                onClickIcon = onClickIcon,
                                onClickItem = onDisconnect,
                                modifier = Modifier.animateItemPlacement(),
                            )

                            BluetoothDeviceState.Connecting,
                            BluetoothDeviceState.Disconnecting -> BluetoothDeviceConnectingItem(
                                device = device,
                                onClickIcon = onClickIcon,
                                onClickItem = onDisconnect,
                                modifier = Modifier.animateItemPlacement(),
                            )

                            else -> BluetoothDeviceDisconnectedItem(
                                device = device,
                                onClickIcon = onClickIcon,
                                onClickItem = onConnect,
                                onSelectConnectionType = onSelectConnectionType,
                                onReselectConnectionType = onReselectConnectionType,
                                modifier = Modifier.animateItemPlacement(),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun launchBluetoothSettingsIntent(context: Context) {
    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    context.startActivity(intent)
}

private suspend fun showDeviceTypeSnackbar(
    snackbarHostState: SnackbarHostState,
    deviceType: String
) {
    snackbarHostState.showSnackbar(
        message = deviceType,
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    )
}