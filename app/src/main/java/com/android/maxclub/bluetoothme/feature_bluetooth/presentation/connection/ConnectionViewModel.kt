package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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

    private val _uiEvent = MutableSharedFlow<ConnectionUiEvent>()
    val uiEvent: SharedFlow<ConnectionUiEvent> = _uiEvent

    private var getStateJob: Job? = null
    private var getDevicesJob: Job? = null
    private var getScanStateJob: Job? = null
    private var scanJob: Job? = null
    private var connectJob: Job? = null

    init {
        getState()

        getDevices()
        getScanState()
        startScan()
    }

    fun onEvent(event: ConnectionEvent) {
        when (event) {
            is ConnectionEvent.OnEnableAdapter -> enableAdapter()
            is ConnectionEvent.OnStartScan -> startScan()
            is ConnectionEvent.OnStopScan -> stopScan()
            is ConnectionEvent.OnOpenBluetoothSettings -> {
                viewModelScope.launch {
                    _uiEvent.emit(ConnectionUiEvent.OnOpenBluetoothSettings)
                }
            }
            is ConnectionEvent.OnClickDeviceIcon -> {
                viewModelScope.launch {
                    _uiEvent.emit(ConnectionUiEvent.OnShowDeviceType(event.deviceType))
                }
            }
            is ConnectionEvent.OnUpdateBluetoothDevice -> {
                bluetoothUseCases.updateBluetoothDevice(event.device)
            }
            is ConnectionEvent.OnConnect -> connect(event.device)
            is ConnectionEvent.OnDisconnect -> disconnect(event.device)
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.value = uiState.value.copy(bluetoothState = state)
                if (state is BluetoothState.On.Connected && state.device != null) {
                    _uiEvent.emit(ConnectionUiEvent.OnConnected(state.device))
                }
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
                if (e is MissingBluetoothPermissionException) {
                    _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
                }
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

    private fun startScan() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                bluetoothUseCases.startScan()
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()
                _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            }
        }
    }

    private fun stopScan() {
        try {
            bluetoothUseCases.stopScan()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            }
        }
    }

    private fun enableAdapter() {
        try {
            bluetoothUseCases.enableAdapter()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            }
        }
    }

    private fun connect(device: BluetoothDevice) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            try {
                bluetoothUseCases.connect(device)
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()
                _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            } catch (e: BluetoothConnectionException) {
                e.printStackTrace()
                _uiEvent.emit(ConnectionUiEvent.OnShowConnectionErrorMessage(e.bluetoothDevice))
            }
        }
    }

    private fun disconnect(device: BluetoothDevice?) {
        try {
            bluetoothUseCases.disconnect(device)
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()
            viewModelScope.launch {
                _uiEvent.emit(ConnectionUiEvent.OnShowMissingPermissionMessage(*e.permissions))
            }
        }
    }
}