package com.tech.maxclub.bluetoothme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tech.maxclub.bluetoothme.feature.main.presentation.main.MainScreenContainer
import com.tech.maxclub.bluetoothme.ui.theme.BluetoothMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            BluetoothMeTheme {
                val statusBarColor = MaterialTheme.colorScheme.primary
                val navigationBarColor = Color.Black

                Surface(
                    color = navigationBarColor,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        color = statusBarColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        MainScreenContainer()
                    }
                }
            }
        }
    }
}