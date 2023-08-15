package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.bluetoothme.core.exceptions.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.core.util.sendIn
import com.android.maxclub.bluetoothme.core.util.update
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.UuidValueValidator
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothLeProfile
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.usecases.bluetooth.BluetoothUseCases
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.BleProfileDialogData
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

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothUseCases: BluetoothUseCases,
    private val uuidValueValidator: UuidValueValidator,
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
            is ConnectionUiEvent.OnStartScan -> {
                startScan()
            }

            is ConnectionUiEvent.OnStopScan -> {
                stopScan()
            }

            is ConnectionUiEvent.OnShowBluetoothSettings -> {
                sendUiAction(ConnectionUiAction.LaunchBluetoothSettingsIntent)
            }

            is ConnectionUiEvent.OnShowDeviceType -> {
                sendUiAction(ConnectionUiAction.ShowDeviceType(event.deviceType))
            }

            is ConnectionUiEvent.OnUpdateBluetoothDevice -> {
                updateBluetoothDevice(event.device)
            }

            is ConnectionUiEvent.OnChangeBleProfileData -> {
                tryChangeBleProfileData(event.data)
            }

            is ConnectionUiEvent.OnShowBleProfileDialog -> {
                setBleProfileDialogData(event.data)
            }

            is ConnectionUiEvent.OnConfirmBleProfileDialog -> {
                _uiState.value.bleProfileDialogData?.let { bleProfileData ->
                    tryUpdateBluetoothLeProfile(bleProfileData, bleProfileData.device)
                }
            }

            is ConnectionUiEvent.OnDismissBleProfileDialog -> {
                setBleProfileDialogData(null)
            }
        }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.update { it.copy(bluetoothState = state) }

                if (state is BluetoothState.On.Connected && state.device != null) {
                    sendUiAction(ConnectionUiAction.ScrollToConnectedDevice(state.device))
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
            .onEach { isScanning ->
                _uiState.update { it.copy(isScanning = isScanning) }
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun getDevices() {
        getDevicesJob?.cancel()
        getDevicesJob = bluetoothUseCases.getBluetoothDevices()
            .onEach { devices ->
                _uiState.update { it.copy(devices = devices) }
            }
            .catch { e ->
                e.printStackTrace()

                if (e is MissingBluetoothPermissionException) {
                    sendUiAction(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
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

                sendUiAction(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
            }
        }
    }

    private fun stopScan() {
        try {
            bluetoothUseCases.stopScan()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            sendUiAction(ConnectionUiAction.RequestMissingPermissions(*e.permissions))
        }
    }

    private fun updateBluetoothDevice(bluetoothDevice: BluetoothDevice) {
        bluetoothUseCases.updateBluetoothDevice(bluetoothDevice)
    }

    private fun tryChangeBleProfileData(data: BleProfileDialogData) {
        val newData = if (uuidValueValidator(data.serviceUuid)
            && uuidValueValidator(data.readCharacteristicUuid)
            && uuidValueValidator(data.writeCharacteristicUuid)
        ) {
            data
        } else {
            _uiState.value.bleProfileDialogData
        }
        setBleProfileDialogData(
            newData?.copy(
                serviceUuidErrorMessage = null,
                readCharacteristicUuidErrorMessage = null,
                writeCharacteristicUuidErrorMessage = null,
            )
        )
    }

    private fun setBleProfileDialogData(data: BleProfileDialogData?) {
        _uiState.update { it.copy(bleProfileDialogData = data) }
    }

    private fun tryUpdateBluetoothLeProfile(
        bleProfileData: BleProfileDialogData,
        device: BluetoothDevice
    ) {
        var serviceUuidErrorMessage: String? = null
        var readCharacteristicUuidErrorMessage: String? = null
        var writeCharacteristicUuidErrorMessage: String? = null

        val connectionType = when (bleProfileData.selectedBleProfileType) {
            BleProfileType.DEFAULT -> {
                ConnectionType.Ble(BluetoothLeProfile.Default)
            }

            BleProfileType.CUSTOM -> {
                val serviceUuid = bleProfileData.serviceUuid.toUuidOrElse {
                    serviceUuidErrorMessage = "Invalid value"
                }
                val readCharacteristicUuid =
                    bleProfileData.readCharacteristicUuid.toUuidOrElse {
                        readCharacteristicUuidErrorMessage = "Invalid value"
                    }
                val writeCharacteristicUuid =
                    bleProfileData.writeCharacteristicUuid.toUuidOrElse {
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
            setBleProfileDialogData(null)
        } else {
            setBleProfileDialogData(
                bleProfileData.copy(
                    serviceUuidErrorMessage = serviceUuidErrorMessage,
                    readCharacteristicUuidErrorMessage = readCharacteristicUuidErrorMessage,
                    writeCharacteristicUuidErrorMessage = writeCharacteristicUuidErrorMessage,
                )
            )
        }
    }

    private fun sendUiAction(uiAction: ConnectionUiAction) {
        uiActionChannel.sendIn(uiAction, viewModelScope)
    }
}