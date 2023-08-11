package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileType
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.toUuidOrElse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_UUID_LENGTH = 50

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        ConnectionUiState(
            bluetoothState = bluetoothUseCases.getState().value,
            devices = emptyList(),
            isScanning = bluetoothUseCases.getScanState().value,
            bleProfileDialogData = null,
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

            is ConnectionUiEvent.OnChangeBleProfileData -> {
                val data = if (event.data.serviceUuid.length <= MAX_UUID_LENGTH
                    && event.data.readCharacteristicUuid.length <= MAX_UUID_LENGTH
                    && event.data.writeCharacteristicUuid.length <= MAX_UUID_LENGTH
                ) {
                    event.data
                } else {
                    _uiState.value.bleProfileDialogData
                }
                _uiState.value = uiState.value.copy(
                    bleProfileDialogData = data?.copy(
                        serviceUuidErrorMessage = null,
                        readCharacteristicUuidErrorMessage = null,
                        writeCharacteristicUuidErrorMessage = null,
                    )
                )
            }

            is ConnectionUiEvent.OnOpenBleProfileDialog -> {
                _uiState.value = uiState.value.copy(
                    bleProfileDialogData = event.data
                )
            }

            is ConnectionUiEvent.OnConfirmBleProfileDialog -> {
                val device = event.data.device

                println(event.data)

                var serviceUuidErrorMessage: String? = null
                var readCharacteristicUuidErrorMessage: String? = null
                var writeCharacteristicUuidErrorMessage: String? = null

                val connectionType = when (event.data.selectedBleProfileType) {
                    BleProfileType.DEFAULT -> {
                        ConnectionType.Ble(BluetoothLeProfile.Default)
                    }

                    BleProfileType.CUSTOM -> {
                        val serviceUuid = event.data.serviceUuid.toUuidOrElse {
                            serviceUuidErrorMessage = "Invalid value"
                        }
                        val readCharacteristicUuid =
                            event.data.readCharacteristicUuid.toUuidOrElse {
                                readCharacteristicUuidErrorMessage = "Invalid value"
                            }
                        val writeCharacteristicUuid =
                            event.data.writeCharacteristicUuid.toUuidOrElse {
                                writeCharacteristicUuidErrorMessage = "Invalid value"
                            }

                        if (serviceUuid != null && readCharacteristicUuid != null && writeCharacteristicUuid != null) {
                            ConnectionType.Ble(
                                BluetoothLeProfile.Custom(
                                    serviceUuid = serviceUuid,
                                    readCharacteristicUuid = readCharacteristicUuid,
                                    writeCharacteristicUuid = writeCharacteristicUuid,
                                )
                            )
                        } else {
                            null
                        }
                    }
                }

                if (connectionType != null) {
                    bluetoothUseCases.updateBluetoothDevice(
                        device.copy(type = device.type.copy(connectionType = connectionType))
                    )
                    _uiState.value = uiState.value.copy(bleProfileDialogData = null)
                } else {
                    _uiState.value = uiState.value.copy(
                        bleProfileDialogData = event.data.copy(
                            serviceUuidErrorMessage = serviceUuidErrorMessage,
                            readCharacteristicUuidErrorMessage = readCharacteristicUuidErrorMessage,
                            writeCharacteristicUuidErrorMessage = writeCharacteristicUuidErrorMessage,
                        )
                    )
                }
            }

            is ConnectionUiEvent.OnDismissBleProfileDialog -> {
                _uiState.value = uiState.value.copy(bleProfileDialogData = null)
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