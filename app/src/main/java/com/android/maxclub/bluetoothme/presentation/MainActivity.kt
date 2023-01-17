package com.android.maxclub.bluetoothme.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.maxclub.bluetoothme.domain.usecase.BluetoothUseCases
import com.android.maxclub.bluetoothme.presentation.connection.ConnectionScreen
import com.android.maxclub.bluetoothme.presentation.util.Screen
import com.android.maxclub.bluetoothme.ui.theme.BluetoothMeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bluetoothUseCases: BluetoothUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.ConnectionScreen.route,
                    ) {
                        composable(route = Screen.ConnectionScreen.route) {
                            ConnectionScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
