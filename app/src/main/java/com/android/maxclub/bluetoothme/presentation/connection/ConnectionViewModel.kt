package com.android.maxclub.bluetoothme.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.domain.usecase.BluetoothUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
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

    init {
        getState()
        getDevices()
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.value = uiState.value.copy(state = state)
            }
            .catch { exception ->
                exception.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getDevices() {
        getDevicesJob?.cancel()
        getDevicesJob = bluetoothUseCases.getBondedDevices()
            .onEach { devices ->
                _uiState.value = uiState.value.copy(devices = devices)
            }
            .catch { exception ->
                exception.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}