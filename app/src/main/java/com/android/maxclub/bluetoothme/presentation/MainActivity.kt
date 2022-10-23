package com.android.maxclub.bluetoothme.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.domain.usecase.BluetoothUseCases
import com.android.maxclub.bluetoothme.ui.theme.BluetoothMeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bluetoothUseCases: BluetoothUseCases

    @SuppressLint("FlowOperatorInvokedInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothMeTheme {
                val devices by bluetoothUseCases.getBondedDevices()
                    .catch { exception ->
                        Toast.makeText(
                            this@MainActivity,
                            exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .collectAsState(initial = emptyList())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        if (devices.isEmpty()) {
                            Text(text = "Empty")
                        } else {
                            LazyColumn {
                                items(items = devices) { device ->
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 50.dp)
                                            .background(color = Color.Gray)
                                            .clickable {
                                                bluetoothUseCases.connectUseCase(device)
                                            },
                                        text = "${device.name} - ${device.type::class.simpleName}(${device.type.connectionType.name}) \t\t\t [${device.state::class.simpleName}]"
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}