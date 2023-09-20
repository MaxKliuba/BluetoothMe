package com.android.maxclub.bluetoothme.feature.main.presentation.main

import android.app.Activity
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
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.ChatScreen
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.AddEditControllerScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.AddEditWidgetScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.ControllerScreen
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.ControllerListScreen
import com.android.maxclub.bluetoothme.feature.main.presentation.main.components.BluetoothPermissionRationaleDialog
import com.android.maxclub.bluetoothme.feature.main.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.NavDrawerItem
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.getNavDrawerItems
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.launchPermissionSettingsIntent
import com.android.maxclub.bluetoothme.feature.main.presentation.navigateToHelpScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContainer() {
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
                    val permissions = action.permissions.toList().toTypedArray()

                    when {
                        action.permissions.toList().toTypedArray().any { permission ->
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                permission
                            )
                        } -> viewModel.showBluetoothPermissionRationaleDialog()

                        else -> permissionResultLauncher.launch(permissions)
                    }
                }

                is MainUiAction.LaunchPermissionSettingsIntent -> {
                    launchPermissionSettingsIntent(context)
                }

                is MainUiAction.LaunchBluetoothAdapterEnableIntent -> {
                    context.startActivity(action.intent)
                }

                is MainUiAction.ShowConnectionErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.connection_error_message,
                            action.device.name
                        ),
                        actionLabel = context.getString(R.string.reconnect_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.connectBluetoothDevice(action.device)
                        }
                    }
                }

                is MainUiAction.ShowSendingErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.send_error_message),
                        actionLabel = if (action.device != null) {
                            context.getString(R.string.reconnect_button)
                        } else {
                            context.getString(R.string.connect_button)
                        },
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            action.device?.let { viewModel.connectBluetoothDevice(it) }
                                ?: navController.navigate(Screen.Connection.route) {
                                    launchSingleTop = true
                                }
                        }
                    }
                }

                is MainUiAction.ShowMessagesCountMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.messages_count_text,
                            action.inputMessagesCount,
                            action.outputMessagesCount
                        ),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }

                is MainUiAction.ShowWidgetDeletedMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.widget_deleted_message),
                        actionLabel = context.getString(R.string.undo_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.tryRestoreWidgetById(action.widgetId)
                        }
                    }
                }

                is MainUiAction.ShowControllerDeletedMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.controller_deleted_message),
                        actionLabel = context.getString(R.string.undo_button),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.tryRestoreControllerById(action.controllerId)
                        }
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
                    navigateToHelpScreen(selectedItem.value, context)
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

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        NavigationDrawer(
            drawerState = drawerState,
            currentDestination = state.currentDestination,
            items = navDrawerItems,
            onSelect = onSelectNavDrawerItem,
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Connection.route,
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
                        onNavigateToController = { controllerId ->
                            navController.navigate("${Screen.Controller.route}/$controllerId")
                        },
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
                            type = NavType.IntType
                            defaultValue = Screen.AddEditController.DEFAULT_CONTROLLER_ID
                        }
                    ),
                ) {
                    AddEditControllerScreen(
                        onNavigateUp = navController::navigateUp,
                        onNavigateToAddEditWidget = { id, isNew, columnsCount ->
                            navController.navigate("${Screen.AddEditWidget.route}/$id/$isNew/$columnsCount")
                        },
                        onDeleteWidget = viewModel::deleteWidget,
                        onDeleteController = viewModel::deleteController,
                    )
                }

                composable(
                    route = Screen.AddEditWidget.routeWithArgs,
                    arguments = listOf(
                        navArgument(name = Screen.AddEditWidget.ARG_ID) {
                            type = NavType.IntType
                        },
                        navArgument(name = Screen.AddEditWidget.ARG_IS_NEW) {
                            type = NavType.BoolType
                        },
                        navArgument(name = Screen.AddEditWidget.ARG_COLUMNS_COUNT) {
                            type = NavType.IntType
                        },
                    ),
                ) {
                    AddEditWidgetScreen(
                        onNavigateUp = navController::navigateUp,
                        onDeleteWidget = viewModel::deleteWidget,
                    )
                }

                composable(
                    route = Screen.Controller.routeWithArgs,
                    arguments = listOf(
                        navArgument(name = Screen.Controller.ARG_CONTROLLER_ID) {
                            type = NavType.IntType
                        }
                    ),
                ) {
                    ControllerScreen(
                        bluetoothState = state.bluetoothState,
                        onNavigateUp = navController::navigateUp,
                        onShowSendingErrorMessage = viewModel::showSendingErrorMessage,
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