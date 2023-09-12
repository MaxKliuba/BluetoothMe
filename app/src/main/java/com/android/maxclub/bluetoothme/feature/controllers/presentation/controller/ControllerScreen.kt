package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.ControllerTopBar
import com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components.WidgetList
import kotlinx.coroutines.flow.collectLatest

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(
    bluetoothState: BluetoothState,
    onNavigateUp: () -> Unit,
    onShowSendingErrorMessage: () -> Unit,
    viewModel: ControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ControllerUiAction.ShowSendingErrorMessage -> {
                    onShowSendingErrorMessage()
                }
                // TODO
            }
        }
    }

    Scaffold(
        topBar = {
            (state as? ControllerUiState.Success)?.let { state ->
                ControllerTopBar(
                    controllerTitle = state.controller.title,
                    bluetoothState = bluetoothState.toString(context),
                    onClickWithAccelerometer = if (state.controller.withAccelerometer) viewModel::enableAccelerometer else null,
                    onClickWithVoiceInput = if (state.controller.withVoiceInput) viewModel::showVoiceInputDialog else null,
                    onClickWithRefresh = if (state.controller.withRefresh) viewModel::refreshState else null,
                    onNavigateUp = onNavigateUp,
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.let { state ->
                when (state) {
                    is ControllerUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is ControllerUiState.Success -> {
                        WidgetList(
                            columnsCount = state.controller.columnsCount.count,
                            widgets = state.widgets,
                            onAction = viewModel::writeMessage,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}