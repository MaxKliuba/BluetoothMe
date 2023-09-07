package com.android.maxclub.bluetoothme.feature.main.presentation.main

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.core.util.Screen
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.ChatScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.AddEditControllerScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.AddEditWidgetScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.ControllerListScreen
import com.android.maxclub.bluetoothme.feature.main.presentation.main.components.BluetoothPermissionRationaleDialog
import com.android.maxclub.bluetoothme.feature.main.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.NavDrawerItem
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.getNavDrawerItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWrapper() {
    val viewModel = hiltViewModel<MainViewModel>()
    val state: MainUiState by viewModel.uiState
    val isBluetoothAdapterEnabled by remember {
        derivedStateOf { state.bluetoothState is BluetoothState.On }
    }
    val isBluetoothDeviceDisconnected by remember {
        derivedStateOf {
            isBluetoothAdapterEnabled && state.bluetoothState is BluetoothState.On.Disconnected
        }
    }
    val isConnectionBadgeEnabled by remember {
        derivedStateOf {
            !isBluetoothAdapterEnabled || state.favoriteBluetoothDevice != null
        }
    }
    val isProgressIndicatorVisible by remember {
        derivedStateOf {
            state.bluetoothState is BluetoothState.TurningOff
                    || state.bluetoothState is BluetoothState.TurningOn
                    || state.bluetoothState is BluetoothState.On.Connecting
                    || state.bluetoothState is BluetoothState.On.Disconnecting
        }
    }
    val isMessageListEmpty by remember { derivedStateOf { state.messagesCount <= 0 } }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    navController.addOnDestinationChangedListener { _, destination, _ ->
        destination.route?.let {
            viewModel.setCurrentDestination(it)
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
                            viewModel.showBluetoothPermissionRationaleDialog()
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

                is MainUiAction.ShowConnectionErrorMessage -> {
                    showConnectionErrorSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        device = action.device,
                        onConnect = viewModel::connectBluetoothDevice,
                    )
                }

                is MainUiAction.ShowSendingErrorMessage -> {
                    showSendingErrorSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        device = action.device,
                        onReconnect = viewModel::connectBluetoothDevice,
                        onNavigateToConnection = {
                            navController.navigate(Screen.Connection.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                is MainUiAction.ShowMessagesCountMessage -> {
                    showMessagesCountSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        inputMessagesCount = action.inputMessagesCount,
                        outputMessagesCount = action.outputMessagesCount
                    )
                }

                is MainUiAction.ShowWidgetDeletedMessage -> {
                    showDeletedItemSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        message = context.getString(R.string.widget_deleted_message),
                    ) {
                        viewModel.tryRestoreWidgetById(action.widgetId)
                    }
                }

                is MainUiAction.ShowControllerDeletedMessage -> {
                    showDeletedItemSnackbar(
                        snackbarHostState = snackbarHostState,
                        context = context,
                        message = context.getString(R.string.controller_deleted_message),
                    ) {
                        viewModel.tryRestoreControllerById(action.controllerId)
                    }
                }
            }
        }
    }

    val onOpenNavigationDrawer: () -> Unit = {
        scope.launch { drawerState.open() }
    }
    val onCloseNavigationDrawer: () -> Unit = {
        scope.launch {
            delay(150)
            drawerState.close()
        }
    }

    val onSelectNavDrawerItem: (NavDrawerItem.Type) -> Unit = remember {
        { selectedItem ->
            when (selectedItem) {
                is NavDrawerItem.Type.Route -> {
                    onCloseNavigationDrawer()
                    navController.navigate(selectedItem.value) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }

                is NavDrawerItem.Type.Url -> {
                    Toast.makeText(context, selectedItem.value, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val navDrawerItems = getNavDrawerItems(
        connectionBadge = NavDrawerItem.Badge.Button(
            onClick = {
                when {
                    !isBluetoothAdapterEnabled -> viewModel.enableBluetoothAdapter()
                    !isBluetoothDeviceDisconnected -> viewModel.disconnectBluetoothDevice(state.favoriteBluetoothDevice)
                    else -> state.favoriteBluetoothDevice?.let { viewModel.connectBluetoothDevice(it) }
                }
            },
            isEnabled = isConnectionBadgeEnabled,
            isProgressIndicatorVisible = isProgressIndicatorVisible,
            getValue = { state.bluetoothState.toString(context) },
        ),
        messageCountBadge = if (!isMessageListEmpty) {
            NavDrawerItem.Badge.Button(
                onClick = viewModel::showMessagesCount,
                getValue = { state.messagesCount.toString() },
            )
        } else {
            null
        }
    )

    if (state.isBluetoothPermissionRationaleDialogVisible) {
        BluetoothPermissionRationaleDialog(
            onDismiss = viewModel::dismissBluetoothPermissionRationaleDialog,
            onConfirm = viewModel::confirmBluetoothPermissionRationaleDialog,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavigationDrawer(
            drawerState = drawerState,
            currentDestination = state.currentDestination,
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
                        onRequestMissingPermissions = viewModel::requestMissingPermissions,
                        onEnableAdapter = viewModel::enableBluetoothAdapter,
                        onConnect = viewModel::connectBluetoothDevice,
                        onDisconnect = viewModel::disconnectBluetoothDevice,
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                    )
                }

                composable(route = Screen.Controllers.route) {
                    ControllerListScreen(
                        onOpenNavigationDrawer = onOpenNavigationDrawer,
                        onNavigateToAddEditController = { controllerId ->
                            navController.navigate(
                                "${Screen.AddEditController.route}?${
                                    Screen.AddEditController.ARG_CONTROLLER_ID
                                }=$controllerId"
                            )
                        },
                        onDeleteController = viewModel::deleteController,
                    )
                }

                composable(
                    route = Screen.AddEditController.routeWithArgs,
                    arguments = listOf(
                        navArgument(name = Screen.AddEditController.ARG_CONTROLLER_ID) {
                            type = NavType.StringType
                            nullable = true
                        }
                    ),
                ) {
                    AddEditControllerScreen(
                        onNavigateUp = navController::navigateUp,
                        onNavigateToAddEditWidget = { id, isNew ->
                            navController.navigate("${Screen.AddEditWidget.route}/$id/$isNew")
                        },
                        onDeleteWidget = viewModel::deleteWidget,
                        onDeleteController = viewModel::deleteController,
                    )
                }

                composable(
                    route = Screen.AddEditWidget.routeWithArgs,
                    arguments = listOf(
                        navArgument(name = Screen.AddEditWidget.ARG_ID) {
                            type = NavType.StringType
                        },
                        navArgument(name = Screen.AddEditWidget.ARG_IS_NEW) {
                            type = NavType.BoolType
                        }
                    ),
                ) {
                    AddEditWidgetScreen(
                        onNavigateUp = navController::navigateUp,
                        onDeleteWidget = viewModel::deleteWidget,
                    )
                }

                composable(route = Screen.Chat.route) {
                    ChatScreen(
                        bluetoothState = state.bluetoothState,
                        onShowSendingErrorMessage = viewModel::showSendingErrorMessage,
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

private suspend fun showConnectionErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    device: BluetoothDevice,
    onConnect: (BluetoothDevice) -> Unit
) {
    snackbarHostState.showSnackbar(
        message = context.getString(R.string.connection_error_message, device.name),
        actionLabel = context.getString(R.string.reconnect_button),
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    ).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
            onConnect(device)
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

private suspend fun showDeletedItemSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    message: String,
    onUndo: () -> Unit
) {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = context.getString(R.string.undo_button),
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    ).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
            onUndo()
        }
    }
}