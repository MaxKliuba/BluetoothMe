package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        ConnectionUiState(
            bluetoothState = bluetoothUseCases.getState().value,
            devices = emptyList(),
            isScanning = bluetoothUseCases.getScanState().value,
        )
    )
    val uiState: State<ConnectionUiState> = _uiState

    private val uiActionChannel = Channel<ConnectionUiAction>()
    val uiAction = uiActionChannel.receiveAsFlow()

    private var getStateJob: Job? = null
    private var getDevicesJob: Job? = null
    private var getScanStateJob: Job? = null
    private var scanJob: Job? = null

    init {
        getState()
        getScanState()
        getDevices()
        startScan()
    }

    fun onEvent(event: ConnectionUiEvent) {
        when (event) {
            is ConnectionUiEvent.OnStartScan -> startScan()
            is ConnectionUiEvent.OnStopScan -> stopScan()
            is ConnectionUiEvent.OnOpenBluetoothSettings -> {
                viewModelScope.launch {
                    uiActionChannel.send(ConnectionUiAction.OpenBluetoothSettings)
                }
            }

            is ConnectionUiEvent.OnClickDeviceIcon -> {
                viewModelScope.launch {
                    uiActionChannel.send(ConnectionUiAction.ShowDeviceType(event.deviceType))
                }
            }

            is ConnectionUiEvent.OnUpdateBluetoothDevice -> {
                bluetoothUseCases.updateBluetoothDevice(event.device)
            }
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.value = uiState.value.copy(bluetoothState = state)
                if (state is BluetoothState.On.Connected && state.device != null) {
                    uiActionChannel.send(ConnectionUiAction.ScrollToConnectedDevice(state.device))
                }
            }
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun getScanState() {
        getScanStateJob?.cancel()
        getScanStateJob = bluetoothUseCases.getScanState()
            .onEach { _uiState.value = uiState.value.copy(isScanning = it) }
            .catch { it.printStackTrace() }
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
                if (e is MissingBluetoothPermissionException) {
                    uiActionChannel.send(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startScan() {
        if (getDevicesJob?.isActive != true) {
            getDevices()
        }

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                bluetoothUseCases.startScan()
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()
                uiActionChannel.send(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
            }
        }
    }

    private fun stopScan() {
        try {
            bluetoothUseCases.stopScan()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                uiActionChannel.send(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
            }
        }
    }
}