package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.WriteMessage
import com.android.maxclub.bluetoothme.feature.controllers.domain.usecases.GetControllerWithWidgetsAndState
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

private const val GET_STATE_TAG = "get"

@HiltViewModel
class ControllerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val getControllerWithWidgetsAndStateUseCase: GetControllerWithWidgetsAndState,
    val writeMessageUseCase: WriteMessage,
) : ViewModel() {

    private val controllerId: Int = savedStateHandle[Screen.Controller.ARG_CONTROLLER_ID]
        ?: Screen.Controller.DEFAULT_CONTROLLER_ID

    private val _uiState = mutableStateOf<ControllerUiState>(ControllerUiState.Loading)
    val uiState: State<ControllerUiState> = _uiState

    private val uiActionChannel = Channel<ControllerUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getControllerWithWidgetsJob: Job? = null

    init {
        (_uiState.value as? ControllerUiState.Loading)?.let {
            getControllerWithWidgetsAndState(controllerId)
        }
    }

    private fun getControllerWithWidgetsAndState(controllerId: Int) {
        getControllerWithWidgetsJob?.cancel()
        getControllerWithWidgetsJob = getControllerWithWidgetsAndStateUseCase(controllerId)
            .onStart {
                _uiState.update { ControllerUiState.Loading }
            }
            .onEach { controllerWithWidgets ->
                _uiState.update {
                    ControllerUiState.Success(
                        controller = controllerWithWidgets.controller,
                        widgets = controllerWithWidgets.widgets,
                    )
                }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    fun enableAccelerometer() {
        // TODO
    }

    fun showVoiceInputDialog() {
        // TODO
    }

    fun refreshState() {
        writeMessage(GET_STATE_TAG, "")
    }

    fun writeMessage(messageTag: String, messageValue: String) {
        try {
            writeMessageUseCase(messageTag, messageValue)
        } catch (e: WriteMessageException) {
            e.printStackTrace()

            uiActionChannel.sendIn(ControllerUiAction.ShowSendingErrorMessage, viewModelScope)
        }
    }
}