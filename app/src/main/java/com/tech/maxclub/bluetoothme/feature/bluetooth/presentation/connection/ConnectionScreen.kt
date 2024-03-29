package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.BleProfileDialog
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.BluetoothDeviceConnectedItem
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.BluetoothDeviceConnectingItem
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.BluetoothDeviceDisconnectedItem
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.ConnectionTopBar
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.EmptyListPlaceholder
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.EnableAdapterPlaceholder
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.EnableLocationDialog
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.connection.components.LocationPermissionDialog
import com.tech.maxclub.bluetoothme.feature.main.presentation.main.components.PermissionRationaleDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    onRequestMissingBluetoothPermissions: (Array<String>) -> Unit,
    onEnableAdapter: () -> Unit,
    onConnect: (BluetoothDevice) -> Unit,
    onDisconnect: (BluetoothDevice?) -> Unit,
    onOpenNavigationDrawer: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel(),
) {
    val state: ConnectionUiState by viewModel.uiState
    val isAdapterEnabled by remember { derivedStateOf { state.bluetoothState is BluetoothState.On } }
    val isLoading by remember { derivedStateOf { state.isLoading } }
    val isDeviceListEmpty by remember { derivedStateOf { state.devices.isEmpty() } }

    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val isAllGranted = results.all { it.value }

        if (isAllGranted) {
            viewModel.startScan()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ConnectionUiAction.RequestMissingBluetoothPermissions -> {
                    onRequestMissingBluetoothPermissions(action.permissions.toList().toTypedArray())
                }

                is ConnectionUiAction.ShowMissingLocationPermissionsDialog -> {
                    val permissions = action.permissions.toList().toTypedArray()

                    when {
                        permissions.any { permission ->
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                permission
                            )
                        } -> viewModel.showLocationPermissionRationaleDialog()

                        else -> viewModel.showLocationPermissionsDialog(*permissions)
                    }
                }

                is ConnectionUiAction.RequestMissingLocationPermissions -> {
                    val permissions = action.permissions.toList().toTypedArray()

                    locationPermissionResultLauncher.launch(permissions)
                }

                is ConnectionUiAction.ShowDeviceType -> {
                    snackbarHostState.showSnackbar(
                        message = action.deviceType,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
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

    val onLaunchBluetoothSettingsIntent: () -> Unit = remember {
        { launchBluetoothSettingsIntent(context) }
    }
    val onClickIcon: (String) -> Unit = remember {
        { viewModel.showDeviceType(it) }
    }
    val onSetConnectionType: (BluetoothDevice, ConnectionType) -> Unit = remember {
        { device, connectionType ->
            viewModel.setConnectionType(device, connectionType)
        }
    }
    val onShowBleProfileDialog: (BluetoothDevice, BluetoothLeProfile) -> Unit = remember {
        { device, bleProfile ->
            viewModel.showBleProfileDialog(device, bleProfile)
        }
    }

    if (state.missingLocationPermissions.isNotEmpty()) {
        LocationPermissionDialog(
            onDismiss = viewModel::dismissLocationPermissionsDialog,
            onAllow = {
                viewModel.requestMissingLocationPermissions(
                    *state.missingLocationPermissions.toTypedArray()
                )
            },
        )
    }

    if (state.isLocationPermissionRationaleDialogVisible) {
        PermissionRationaleDialog(
            title = stringResource(id = R.string.location_permission_dialog_title),
            text = stringResource(id = R.string.location_permission_dialog_text),
            onDismiss = viewModel::dismissLocationPermissionRationaleDialog,
        )
    }

    if (state.isEnableLocationDialogVisible) {
        EnableLocationDialog(onDismiss = viewModel::dismissEnableLocationDialog)
    }

    state.bleProfileDialogData?.let { bleProfileDialogData ->
        BleProfileDialog(
            data = bleProfileDialogData,
            onChangeBleProfileData = viewModel::tryChangeBleProfileData,
            onDismiss = viewModel::dismissBleProfileDialog,
            onConfirm = viewModel::confirmBleProfileDialog,
        )
    }

    Scaffold(
        topBar = {
            ConnectionTopBar(
                isAdapterEnabled = isAdapterEnabled,
                isScanning = state.isScanning,
                onStartScan = viewModel::startScan,
                onStopScan = viewModel::stopScan,
                onOpenBluetoothSettings = onLaunchBluetoothSettingsIntent,
                onOpenNavigationDrawer = onOpenNavigationDrawer,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !isAdapterEnabled -> {
                    EnableAdapterPlaceholder(
                        onEnableAdapter = onEnableAdapter,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                isDeviceListEmpty -> {
                    EmptyListPlaceholder(
                        onStartScan = viewModel::startScan,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                !isDeviceListEmpty -> {
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
                                    onSetConnectionType = onSetConnectionType,
                                    onShowBleProfileDialog = onShowBleProfileDialog,
                                    modifier = Modifier.animateItemPlacement(),
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

private fun launchBluetoothSettingsIntent(context: Context) {
    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    context.startActivity(intent)
}