package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controller

import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.controller.components.ControllerTopBar
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.controller.components.WidgetList
import com.tech.maxclub.bluetoothme.ui.components.BaseScaffold
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(
    bluetoothState: BluetoothState,
    onNavigateUp: () -> Unit,
    onShowSendingErrorMessage: () -> Unit,
    viewModel: ControllerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState
    val withRefreshButton by remember {
        derivedStateOf { (state as? ControllerUiState.Success)?.controller?.withRefresh != null }
    }

    val context = LocalContext.current

    val speechRecognizerIntentResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { words ->
                val messageValue = words.joinToString(separator = " ")
                viewModel.writeVoiceInputMessage(messageValue)
            }
        }
    }

    LaunchedEffect(bluetoothState, withRefreshButton) {
        if (withRefreshButton && bluetoothState is BluetoothState.On.Connected) {
            viewModel.refreshState()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ControllerUiAction.ShowSendingErrorMessage -> {
                    onShowSendingErrorMessage()
                }

                is ControllerUiAction.LaunchSpeechRecognizerIntent -> {
                    speechRecognizerIntentResultLauncher.launch(action.intent)
                }
            }
        }
    }

    BaseScaffold(
        topBar = {
            (state as? ControllerUiState.Success)?.let { state ->
                ControllerTopBar(
                    controllerTitle = state.controller.title,
                    bluetoothState = bluetoothState.toString(context),
                    onClickWithAccelerometer = null,
                    onClickWithVoiceInput = if (state.controller.withVoiceInput) viewModel::showSpeechRecognizerDialog else null,
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