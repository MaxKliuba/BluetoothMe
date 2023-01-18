package com.android.maxclub.bluetoothme.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.domain.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothDevice
import com.android.maxclub.bluetoothme.domain.usecase.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.domain.usecase.messages.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
    private val messagesUseCases: MessagesUseCases,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        ConnectionUiState(
            state = bluetoothUseCases.getState().value,
            devices = emptyList(),
        )
    )
    val uiState: State<ConnectionUiState> = _uiState

    private var getStateJob: Job? = null
    private var getDevicesJob: Job? = null
    private var getMessagesJob: Job? = null

    init {
        getState()
        getDevices()
        getMessages()
    }

    fun onConnect(device: BluetoothDevice) =
        viewModelScope.launch {
            try {
                bluetoothUseCases.connect(device)
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()
            }
        }

    fun onDisconnect(device: BluetoothDevice) =
        viewModelScope.launch {
            try {
                bluetoothUseCases.disconnect(device)
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()
            }
        }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.value = uiState.value.copy(state = state)
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getDevices() {
        getDevicesJob?.cancel()
        getDevicesJob = bluetoothUseCases.getBluetoothDevices()
            .onEach { devices ->
                _uiState.value = uiState.value.copy(devices = devices)
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getMessages() {
        getMessagesJob?.cancel()
        getMessagesJob = messagesUseCases.getMessages()
            .onEach { messages ->
                println(messages)
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}