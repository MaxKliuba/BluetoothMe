package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.ChatScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.controllers.ControllersScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.components.BluetoothPermissionRationaleDialog
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerBadge
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerItem
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerItemType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWrapper() {
    val viewModel = hiltViewModel<MainViewModel>()
    val state: MainUiState by viewModel.uiState

    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    navController.addOnDestinationChangedListener { _, destination, _ ->
        destination.route?.let {
            viewModel.onEvent(MainUiEvent.OnDestinationChanged(it))
        }
    }

    val permissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is MainUiAction.RequestMissingPermissions -> {
                    requestMissingPermissions(
                        permissions = action.permissions.toList().toTypedArray(),
                        context = context,
                        onShowRationaleDialog = {
                            viewModel.onEvent(MainUiEvent.OnShowBluetoothPermissionRationaleDialog)
                        },
                        onLaunchPermissionResultLauncher = {
                            permissionResultLauncher.launch(it)
                        }
                    )
                }

                is MainUiAction.LaunchPermissionSettingsIntent -> {
                    launchPermissionSettingsIntent(context)
                }

                is MainUiAction.LaunchBluetoothAdapterEnableIntent -> {
                    context.startActivity(action.intent)
                }

                is MainUiAction.OpenNavigationDrawer -> {
                    launch { drawerState.open() }
                }

                is MainUiAction.NavigateToSelectedNavDrawerItem -> {
                    navigateToSelectedNawDrawerItem(
                        selectedItem = action.selectedItem,
                        onCloseNavDrawer = {
                            launch {
                                delay(150)
                                drawerState.close()
                            }
                        },
                        onNavigate = {
                            navController.navigate(it) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        context = context
                    )
                }

                is MainUiAction.ShowConnectionErrorMessage -> {
                    showConnectionErrorSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        deviceName = action.device.name
                    ) {
                        viewModel.onEvent(MainUiEvent.OnConnect(action.device))
                    }
                }

                is MainUiAction.ShowSendingErrorMessage -> {
                    showSendingErrorSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        device = action.device,
                        onReconnect = { viewModel.onEvent(MainUiEvent.OnConnect(it)) },
                        onNavigateToConnection = {
                            navController.navigate(Screen.Connection.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                is MainUiAction.ShowMessagesCount -> {
                    showMessagesCountSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        inputMessagesCount = action.inputMessagesCount,
                        outputMessagesCount = action.outputMessagesCount
                    )
                }
            }
        }
    }

    val onOpenNavigationDrawer: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnOpenNavigationDrawer) }
    }
    val onRequestMissingPermissions: (Array<String>) -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnRequestMissingPermissions(*it)) }
    }
    val onShowSendingErrorMessage: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnShowSendingErrorMessage) }
    }
    val onSelectNavDrawerItem: (NavDrawerItemType) -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnSelectNavDrawerItem(it)) }
    }
    val onEnableAdapter: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnEnableAdapter) }
    }
    val onConnect: (BluetoothDevice) -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnConnect(it)) }
    }
    val onDisconnect: (BluetoothDevice?) -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnDisconnect(it)) }
    }

    val onClickConnectionBadge: () -> Unit = remember {
        {
            when (state.bluetoothState) {
                is BluetoothState.TurningOff,
                is BluetoothState.Off,
                is BluetoothState.TurningOn -> {
                    onEnableAdapter()
                }

                is BluetoothState.On.Connecting,
                is BluetoothState.On.Connected,
                is BluetoothState.On.Disconnecting -> {
                    onDisconnect(state.favoriteBluetoothDevice)
                }

                is BluetoothState.On.Disconnected -> {
                    state.favoriteBluetoothDevice?.let { onConnect(it) }
                }
            }
        }
    }
    val getConnectionState: () -> String = remember {
        { state.bluetoothState.toString(context) }
    }
    val onClickMessagesBadge: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnShowMessagesCount) }
    }
    val getMessagesCount: () -> String = remember {
        { state.messagesCount.toString() }
    }

    val navDrawerItems = listOf(
        NavDrawerItem(
            type = NavDrawerItemType.Route(Screen.Connection.route),
            icon = R.drawable.ic_bluetooth_connection_24,
            label = R.string.connection_screen_title,
            badge = NavDrawerBadge.Button(
                onClick = onClickConnectionBadge,
                isEnabled = state.bluetoothState !is BluetoothState.On || state.favoriteBluetoothDevice != null,
                withProgressIndicator = when (state.bluetoothState) {
                    is BluetoothState.TurningOff,
                    is BluetoothState.TurningOn,
                    is BluetoothState.On.Connecting,
                    is BluetoothState.On.Disconnecting -> true

                    else -> false
                },
                getValue = getConnectionState,
            ),
        ),
        NavDrawerItem(
            type = NavDrawerItemType.Route(Screen.Controllers.route),
            icon = R.drawable.ic_controllers_24,
            label = R.string.controllers_screen_title,
            badge = null,
        ),
        NavDrawerItem(
            type = NavDrawerItemType.Route(Screen.Chat.route),
            icon = R.drawable.ic_chat_24,
            label = R.string.chat_screen_title,
            badge = if (state.messagesCount > 0) {
                NavDrawerBadge.Button(
                    onClick = onClickMessagesBadge,
                    getValue = getMessagesCount,
                )
            } else {
                null
            },
        ),
        NavDrawerItem(
            type = NavDrawerItemType.Url(Screen.Help.route),
            icon = R.drawable.ic_help_24,
            label = R.string.help_screen_title,
            badge = null,
        ),
    )

    val onDismissBluetoothPermissionRationaleDialog: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnDismissBluetoothPermissionRationaleDialog) }
    }
    val onConfirmBluetoothPermissionRationaleDialog: () -> Unit = remember {
        { viewModel.onEvent(MainUiEvent.OnConfirmBluetoothPermissionRationaleDialog) }
    }

    if (state.isBluetoothPermissionRationaleDialogVisible) {
        BluetoothPermissionRationaleDialog(
            onDismiss = onDismissBluetoothPermissionRationaleDialog,
            onConfirm = onConfirmBluetoothPermissionRationaleDialog,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavigationDrawer(
            drawerState = drawerState,
            selectedItem = state.selectedNavDrawerItem,
            items = navDrawerItems,
            onSelect = onSelectNavDrawerItem,
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Connection.route
            ) {
                composable(route = Screen.Connection.route) {
                    ConnectionScreen(
                        onRequestMissingPermissions = onRequestMissingPermissions,
                        onEnableAdapter = onEnableAdapter,
                        onConnect = onConnect,
                        onDisconnect = onDisconnect,
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                    )
                }

                composable(route = Screen.Controllers.route) {
                    ControllersScreen(
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                    )
                }

                composable(route = Screen.Chat.route) {
                    ChatScreen(
                        bluetoothState = state.bluetoothState,
                        onShowSendingErrorMessage = onShowSendingErrorMessage,
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                    )
                }
            }
        }
    }
}

private fun requestMissingPermissions(
    permissions: Array<String>,
    context: Context,
    onShowRationaleDialog: () -> Unit,
    onLaunchPermissionResultLauncher: (Array<String>) -> Unit
) {
    when {
        permissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission
            )
        } -> onShowRationaleDialog()

        else -> onLaunchPermissionResultLauncher(permissions)
    }
}

private fun launchPermissionSettingsIntent(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

private fun navigateToSelectedNawDrawerItem(
    selectedItem: NavDrawerItemType,
    onCloseNavDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    context: Context
) {
    when (selectedItem) {
        is NavDrawerItemType.Route -> {
            onCloseNavDrawer()
            onNavigate(selectedItem.value)
        }

        is NavDrawerItemType.Url -> {
            Toast.makeText(context, selectedItem.value, Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun showConnectionErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    deviceName: String,
    onConnect: () -> Unit
) {
    snackbarHostState.showSnackbar(
        message = context.getString(R.string.connection_error_message, deviceName),
        actionLabel = context.getString(R.string.reconnect_button),
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    ).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
            onConnect()
        }
    }
}

private suspend fun showSendingErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    device: BluetoothDevice?,
    onReconnect: (BluetoothDevice) -> Unit,
    onNavigateToConnection: () -> Unit
) {
    snackbarHostState.showSnackbar(
        message = context.getString(R.string.send_error_message),
        actionLabel = if (device != null) {
            context.getString(R.string.reconnect_button)
        } else {
            context.getString(R.string.connect_button)
        },
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    ).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
            device?.let { onReconnect(it) } ?: onNavigateToConnection()
        }
    }
}

private suspend fun showMessagesCountSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    inputMessagesCount: Int,
    outputMessagesCount: Int
) {
    snackbarHostState.showSnackbar(
        message = context.getString(
            R.string.messages_count_text,
            inputMessagesCount,
            outputMessagesCount
        ),
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    )
}