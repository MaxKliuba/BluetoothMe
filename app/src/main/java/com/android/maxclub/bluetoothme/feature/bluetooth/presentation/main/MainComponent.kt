package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main

import android.app.Activity
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.controllers.ControllersScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.components.BluetoothPermissionRationaleDialog
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerBadge
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.main.util.NavDrawerItem
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.ChatScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainComponent(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state: MainUiState by viewModel.uiState
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    navController.addOnDestinationChangedListener { _, destination, _ ->
        destination.route?.let {
            viewModel.onEvent(MainUiEvent.OnDestinationChanged(it))
        }
    }

    val permissionResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
    }

    val onRequestMissingPermissions: (Array<String>) -> Unit = { permissions ->
        when {
            permissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
            } -> viewModel.onEvent(MainUiEvent.OnOpenBluetoothPermissionRationaleDialog)

            else -> permissionResultLauncher.launch(permissions)
        }
    }

    val onShowConnectionErrorMessage: (BluetoothDevice) -> Unit = { bluetoothDevice ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(
                    R.string.connection_error_message,
                    bluetoothDevice.name
                ),
                actionLabel = context.getString(R.string.reconnect_button),
                withDismissAction = true,
                duration = SnackbarDuration.Short,
            ).let { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onEvent(MainUiEvent.OnConnect(bluetoothDevice))
                }
            }
        }
    }

    val onShowSendErrorMessage: () -> Unit = remember {
        {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.send_error_message),
                    actionLabel = context.getString(if (state.favoriteBluetoothDevice != null) R.string.reconnect_button else R.string.connect_button),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short,
                ).let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        state.favoriteBluetoothDevice?.let {
                            viewModel.onEvent(MainUiEvent.OnConnect(it))
                        } ?: navController.navigate(Screen.Connection.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is MainUiAction.RequestMissingPermissions -> {
                    onRequestMissingPermissions(action.permissions.toList().toTypedArray())
                }

                is MainUiAction.LaunchIntent -> {
                    context.startActivity(action.intent)
                }

                is MainUiAction.ShowConnectionErrorMessage -> {
                    onShowConnectionErrorMessage(action.device)
                }

                is MainUiAction.ShowMessagesCount -> {
                    launch(Dispatchers.Main) {
                        snackbarHostState.showSnackbar(
                            message = """
                                |Input messages: ${action.inputMessagesCount}
                                |Output messages: ${action.outputMessagesCount}""".trimMargin(),
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
        }
    }

    val onOpenNavigationDrawer: () -> Unit = remember {
        { scope.launch { drawerState.open() } }
    }
    val onNavigate: (String) -> Unit = {
        scope.launch {
            delay(150)
            drawerState.close()
        }

        navController.navigate(it) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }
    val onLaunchUrl: (String) -> Unit = {
        scope.launch {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val onEnableAdapter: () -> Unit = {
        viewModel.onEvent(MainUiEvent.OnEnableAdapter)
    }
    val onConnect: (BluetoothDevice) -> Unit = {
        viewModel.onEvent(MainUiEvent.OnConnect(it))
    }
    val onDisconnect: (BluetoothDevice?) -> Unit = {
        viewModel.onEvent(MainUiEvent.OnDisconnect(it))
    }
    val onClickConnectionBadge: () -> Unit = {
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
    val getConnectionState: () -> String = {
        state.bluetoothState.toString(context)
    }
    val onClickMessagesBadge: () -> Unit = {
        viewModel.onEvent(MainUiEvent.OnClickMessagesCountBudge)
    }
    val getMessagesCount: () -> String = {
        state.messagesCount.toString()
    }

    val navDrawerItems = listOf(
        NavDrawerItem(
            route = Screen.Connection.route,
            icon = R.drawable.ic_bluetooth_connection_24,
            label = R.string.connection_screen_title,
            badge = NavDrawerBadge.Button(
                onClick = onClickConnectionBadge,
                isEnabled = state.bluetoothState !is BluetoothState.On
                        || state.favoriteBluetoothDevice != null,
                withIndicator = when (state.bluetoothState) {
                    is BluetoothState.TurningOff,
                    is BluetoothState.TurningOn,
                    is BluetoothState.On.Connecting,
                    is BluetoothState.On.Disconnecting -> true

                    else -> false
                },
                getValue = getConnectionState,
            ),
            onClick = onNavigate
        ),
        NavDrawerItem(
            route = Screen.Controllers.route,
            icon = R.drawable.ic_controllers_24,
            label = R.string.controllers_screen_title,
            badge = null,
            onClick = onNavigate
        ),
        NavDrawerItem(
            route = Screen.Chat.route,
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
            onClick = onNavigate
        ),
        NavDrawerItem(
            route = Screen.Help.route,
            icon = R.drawable.ic_help_24,
            label = R.string.help_screen_title,
            badge = null,
            onClick = onLaunchUrl
        ),
    )

    if (state.isBluetoothPermissionRationaleDialogOpen) {
        BluetoothPermissionRationaleDialog(
            onDismiss = { viewModel.onEvent(MainUiEvent.OnCloseBluetoothPermissionRationaleDialog) },
            onConfirm = {
                viewModel.onEvent(MainUiEvent.OnCloseBluetoothPermissionRationaleDialog)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavigationDrawer(
            selectedItem = state.selectedNavDrawerItem,
            items = navDrawerItems,
            drawerState = drawerState,
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
                        onClickNavigationIcon = onOpenNavigationDrawer,
                    )
                }

                composable(route = Screen.Controllers.route) {
                    ControllersScreen(
                        onClickNavigationIcon = onOpenNavigationDrawer,
                    )
                }

                composable(route = Screen.Chat.route) {
                    ChatScreen(
                        bluetoothState = state.bluetoothState,
                        onShowSendErrorMessage = onShowSendErrorMessage,
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                    )
                }
            }
        }
    }
}