package com.android.maxclub.bluetoothme.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import com.android.maxclub.bluetoothme.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.presentation.main.components.NavigationDrawer
import com.android.maxclub.bluetoothme.presentation.terminal.TerminalScreen
import com.android.maxclub.bluetoothme.presentation.util.*
import com.android.maxclub.bluetoothme.ui.theme.BluetoothMeTheme
import com.android.maxclub.bluetoothme.util.toString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()

                    val state: MainUiState by viewModel.uiState
                    val navController = rememberNavController()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    val openNavigationDrawer: () -> Unit = { scope.launch { drawerState.open() } }
                    val closeNavigationDrawer: () -> Unit = {
                        scope.launch {
                            delay(150)
                            drawerState.close()
                        }
                    }

                    val navigationDrawerItems = listOf(
                        NavigationDrawerItem(
                            route = Screen.ConnectionScreen.route,
                            icon = R.drawable.ic_bluetooth_connection_24,
                            label = R.string.connection_screen_title,
                            badge = Badge.Button(
                                onClick = {
                                    when (state.bluetoothState) {
                                        is BluetoothState.TurningOff,
                                        is BluetoothState.Off,
                                        is BluetoothState.TurningOn -> {
                                        }
                                        is BluetoothState.On.Connecting,
                                        is BluetoothState.On.Connected,
                                        is BluetoothState.On.Disconnecting -> {
                                            viewModel.onDisconnect(state.favoriteBluetoothDevice)
                                        }
                                        is BluetoothState.On.Disconnected -> {
                                            state.favoriteBluetoothDevice?.let {
                                                viewModel.onConnect(it)
                                            }
                                        }
                                    }
                                },
                                isEnabled = state.bluetoothState is BluetoothState.On && state.favoriteBluetoothDevice != null,
                                withIndicator = when (state.bluetoothState) {
                                    is BluetoothState.TurningOff,
                                    is BluetoothState.TurningOn,
                                    is BluetoothState.On.Connecting,
                                    is BluetoothState.On.Disconnecting -> true
                                    else -> false
                                }
                            ) { state.bluetoothState.toString(context) },
                            onClick = {
                                navController.smartNavigate(Screen.ConnectionScreen.route)
                                closeNavigationDrawer()
                            }
                        ),
                        NavigationDrawerItem(
                            route = Screen.ControllersScreen.route,
                            icon = R.drawable.ic_controllers_24,
                            label = R.string.controllers_screen_title,
                            badge = null,
                            onClick = {
                                closeNavigationDrawer()
                            }
                        ),
                        NavigationDrawerItem(
                            route = Screen.TerminalScreen.route,
                            icon = R.drawable.ic_terminal_24,
                            label = R.string.terminal_screen_title,
                            badge = Badge.Button(onClick = { /*TODO*/ }) { state.messagesCount.toStringOrEmpty() },
                            onClick = {
                                navController.smartNavigate(Screen.TerminalScreen.route)
                                closeNavigationDrawer()
                            }
                        ),
                        NavigationDrawerItem(
                            route = null,
                            icon = R.drawable.ic_help_24,
                            label = R.string.help_screen_title,
                            badge = null,
                            onClick = {
                                // TODO
                                closeNavigationDrawer()
                            }
                        ),
                    )

                    NavHost(
                        navController = navController,
                        startDestination = Screen.ConnectionScreen.route,
                    ) {
                        composable(route = Screen.ConnectionScreen.route) {
                            NavigationDrawer(
                                currentRoute = it.destination.route,
                                items = navigationDrawerItems,
                                drawerState = drawerState,
                            ) {
                                ConnectionScreen(onClickNavigationIcon = openNavigationDrawer)
                            }
                        }
                        composable(route = Screen.TerminalScreen.route) {
                            NavigationDrawer(
                                currentRoute = it.destination.route,
                                items = navigationDrawerItems,
                                drawerState = drawerState,
                            ) {
                                TerminalScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
