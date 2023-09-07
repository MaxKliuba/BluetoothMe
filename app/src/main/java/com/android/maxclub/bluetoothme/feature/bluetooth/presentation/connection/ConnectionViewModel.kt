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
import com.android.maxclub.bluetoothme.core.util.toUuidOrElse
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util.toBleProfileType
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

    fun startScan() {
        if (getDevicesJob?.isActive != true) {
            getDevices()
        }

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                bluetoothUseCases.startScan()
            } catch (e: MissingBluetoothPermissionException) {
                e.printStackTrace()

                uiActionChannel.sendIn(
                    ConnectionUiAction.RequestMissingPermissions(*e.permissions),
                    viewModelScope
                )
            }
        }
    }

    fun stopScan() {
        try {
            bluetoothUseCases.stopScan()
        } catch (e: MissingBluetoothPermissionException) {
            e.printStackTrace()

            uiActionChannel.sendIn(
                ConnectionUiAction.RequestMissingPermissions(*e.permissions),
                viewModelScope
            )
        }
    }

    fun showDeviceType(deviceType: String) {
        uiActionChannel.sendIn(ConnectionUiAction.ShowDeviceType(deviceType), viewModelScope)
    }

    fun setConnectionType(bluetoothDevice: BluetoothDevice, newConnectionType: ConnectionType) {
        bluetoothUseCases.updateBluetoothDevice(
            bluetoothDevice.copy(type = bluetoothDevice.type.copy(connectionType = newConnectionType))
        )
    }

    fun showBleProfileDialog(bluetoothDevice: BluetoothDevice, bleProfile: BluetoothLeProfile) {
        val data = BleProfileDialogData(
            device = bluetoothDevice,
            selectedBleProfileType = bleProfile.toBleProfileType(),
            serviceUuid = (bleProfile as? BluetoothLeProfile.Custom)?.serviceUuid?.toString() ?: "",
            readCharacteristicUuid = (bleProfile as? BluetoothLeProfile.Custom)
                ?.readCharacteristicUuid?.toString() ?: "",
            writeCharacteristicUuid = (bleProfile as? BluetoothLeProfile.Custom)
                ?.writeCharacteristicUuid?.toString() ?: "",
        )
        _uiState.update { it.copy(bleProfileDialogData = data) }
    }

    fun tryChangeBleProfileData(data: BleProfileDialogData): Boolean {
        val result: Boolean

        val newData = if (uuidValueValidator(data.serviceUuid)
            && uuidValueValidator(data.readCharacteristicUuid)
            && uuidValueValidator(data.writeCharacteristicUuid)
        ) {
            result = true
            data
        } else {
            result = false
            _uiState.value.bleProfileDialogData
        }

        _uiState.update {
            it.copy(
                bleProfileDialogData = newData?.copy(
                    serviceUuidErrorMessage = null,
                    readCharacteristicUuidErrorMessage = null,
                    writeCharacteristicUuidErrorMessage = null,
                )
            )
        }

        return result
    }

    fun confirmBleProfileDialog(bleProfileData: BleProfileDialogData) {
        if (tryUpdateBluetoothLeProfile(bleProfileData)) {
            dismissBleProfileDialog()
        }
    }

    fun dismissBleProfileDialog() {
        _uiState.update { it.copy(bleProfileDialogData = null) }
    }

    private fun getState() {
        getStateJob?.cancel()
        getStateJob = bluetoothUseCases.getState()
            .onEach { state ->
                _uiState.update { it.copy(bluetoothState = state) }

                if (state is BluetoothState.On.Connected && state.device != null) {
                    uiActionChannel.sendIn(
                        ConnectionUiAction.ScrollToConnectedDevice(state.device),
                        viewModelScope
                    )
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
                    uiActionChannel.sendIn(
                        ConnectionUiAction.RequestMissingPermissions(*e.permissions),
                        viewModelScope
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun tryUpdateBluetoothLeProfile(bleProfileData: BleProfileDialogData): Boolean {
        val device = bleProfileData.device

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

        return if (connectionType != null) {
            bluetoothUseCases.updateBluetoothDevice(
                device.copy(type = device.type.copy(connectionType = connectionType))
            )

            true
        } else {
            _uiState.update {
                it.copy(
                    bleProfileDialogData = bleProfileData.copy(
                        serviceUuidErrorMessage = serviceUuidErrorMessage,
                        readCharacteristicUuidErrorMessage = readCharacteristicUuidErrorMessage,
                        writeCharacteristicUuidErrorMessage = writeCharacteristicUuidErrorMessage,
                    )
                )

            }

            false
        }
    }
}