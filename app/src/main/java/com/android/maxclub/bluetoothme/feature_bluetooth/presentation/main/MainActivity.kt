package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.controllers.ControllersScreen
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.terminal.TerminalScreen
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.util.*
import com.android.maxclub.bluetoothme.ui.theme.BluetoothMeTheme
import com.android.maxclub.bluetoothme.feature_bluetooth.util.toString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val HELP_URL = "url"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    val state: MainUiState by viewModel.uiState
                    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
                    val navController = rememberNavController()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    LaunchedEffect(key1 = true) {
                        viewModel.uiEvent.collect { event ->
                            when (event) {
                                is MainUiEvent.OnShowMissingPermissionMessage -> {
                                    Toast.makeText(
                                        context,
                                        event.permissions.joinToString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is MainUiEvent.OnLaunchIntent -> {
                                    startActivity(event.intent)
                                }
                                is MainUiEvent.OnShowConnectionErrorMessage -> {
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
                                                viewModel.onEvent(MainEvent.OnConnect(event.device))
                                            }
                                        }
                                    }
                                }
                                is MainUiEvent.OnOpenNavigationDrawer -> {
                                    launch(Dispatchers.Main) { drawerState.open() }
                                }
                                is MainUiEvent.OnShowMessagesCount -> {
                                    launch(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar(
                                            message = "${event.inputMessagesCount} input messages, ${event.outputMessagesCount} output messages",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short,
                                        )
                                    }
                                }
                                is MainUiEvent.OnNavigate -> {
                                    navController.smartNavigate(event.route)
                                    launch(Dispatchers.Main) {
                                        delay(150)
                                        drawerState.close()
                                    }
                                }
                                is MainUiEvent.OnLaunchUrl -> {
                                    Toast.makeText(
                                        context,
                                        event.url,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    val onClickNavigationIcon: () -> Unit = {
                        viewModel.onEvent(MainEvent.OnClickNavigationIcon)
                    }
                    val onNavigate: (String) -> Unit = {
                        viewModel.onEvent(MainEvent.OnNavigate(it))
                    }
                    val onLaunchUrl: (String) -> Unit = {
                        viewModel.onEvent(MainEvent.OnLaunchUrl(it))
                    }
                    val onClickConnectionBadge: () -> Unit = {
                        when (state.bluetoothState) {
                            is BluetoothState.TurningOff,
                            is BluetoothState.Off,
                            is BluetoothState.TurningOn -> {
                                viewModel.onEvent(MainEvent.OnEnableAdapter)
                            }
                            is BluetoothState.On.Connecting,
                            is BluetoothState.On.Connected,
                            is BluetoothState.On.Disconnecting -> {
                                viewModel.onEvent(MainEvent.OnDisconnect(state.favoriteBluetoothDevice))
                            }
                            is BluetoothState.On.Disconnected -> {
                                state.favoriteBluetoothDevice?.let {
                                    viewModel.onEvent(MainEvent.OnConnect(it))
                                }
                            }
                        }
                    }
                    val getConnectionState: () -> String = {
                        state.bluetoothState.toString(context)
                    }
                    val onClickMessagesBadge: () -> Unit = {
                        viewModel.onEvent(MainEvent.OnClickMessagesCountBudge)
                    }
                    val getMessagesCount: () -> String = {
                        state.messagesCount.toString()
                    }

                    val navigationDrawerItems = listOf(
                        NavigationDrawerItem(
                            route = Screen.ConnectionScreen.route,
                            icon = R.drawable.ic_bluetooth_connection_24,
                            label = R.string.connection_screen_title,
                            badge = Badge.Button(
                                onClick = onClickConnectionBadge,
                                isEnabled = state.bluetoothState !is BluetoothState.On || state.favoriteBluetoothDevice != null,
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
                        NavigationDrawerItem(
                            route = Screen.ControllersScreen.route,
                            icon = R.drawable.ic_controllers_24,
                            label = R.string.controllers_screen_title,
                            badge = null,
                            onClick = onNavigate
                        ),
                        NavigationDrawerItem(
                            route = Screen.TerminalScreen.route,
                            icon = R.drawable.ic_terminal_24,
                            label = R.string.terminal_screen_title,
                            badge = if (state.messagesCount > 0) {
                                Badge.Button(
                                    onClick = onClickMessagesBadge,
                                    getValue = getMessagesCount,
                                )
                            } else {
                                null
                            },
                            onClick = onNavigate
                        ),
                        NavigationDrawerItem(
                            route = HELP_URL,
                            icon = R.drawable.ic_help_24,
                            label = R.string.help_screen_title,
                            badge = null,
                            onClick = onLaunchUrl
                        ),
                    )

                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.ConnectionScreen.route,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(route = Screen.ConnectionScreen.route) {
                                NavigationDrawer(
                                    currentRoute = it.destination.route,
                                    items = navigationDrawerItems,
                                    drawerState = drawerState,
                                ) {
                                    ConnectionScreen(
                                        onClickNavigationIcon = onClickNavigationIcon,
                                    )
                                }
                            }
                            composable(route = Screen.ControllersScreen.route) {
                                NavigationDrawer(
                                    currentRoute = it.destination.route,
                                    items = navigationDrawerItems,
                                    drawerState = drawerState,
                                ) {
                                    ControllersScreen()
                                }
                            }
                            composable(route = Screen.TerminalScreen.route) {
                                NavigationDrawer(
                                    currentRoute = it.destination.route,
                                    items = navigationDrawerItems,
                                    drawerState = drawerState,
                                ) {
                                    TerminalScreen(
                                        onClickNavigationIcon = onClickNavigationIcon,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
