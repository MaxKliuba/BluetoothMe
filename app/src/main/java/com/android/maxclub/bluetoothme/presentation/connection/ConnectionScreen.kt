package com.android.maxclub.bluetoothme.presentation.connection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConnectionScreen(
    navController: NavController,
    viewModel: ConnectionViewModel = hiltViewModel(),
) {
    val state: ConnectionUiState by viewModel.uiState

    Column {
        if (state.devices.isEmpty()) {
            Text(text = "Empty")
        } else {
            LazyColumn {
                items(
                    items = state.devices,
                    key = { it.address },
                ) { device ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .background(color = Color.Gray)
                            .clickable {
                                // TODO Connection
                            }
                            .animateItemPlacement(),
                        text = "${device.name} - ${device.type::class.simpleName}(${device.type.connectionType.name}) \t\t\t [${device.state::class.simpleName}]"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
