package com.tech.maxclub.bluetoothme.feature.bluetooth.data.repositories

import com.tech.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.tech.maxclub.bluetoothme.di.BluetoothClassic
import com.tech.maxclub.bluetoothme.di.BluetoothLe
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothDeviceService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.*
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessagesDataSource
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.ConnectionType
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.repositories.BluetoothRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepositoryImpl @Inject constructor(
    private val bluetoothAdapterManager: BluetoothAdapterManager,
    private val bluetoothDeviceService: BluetoothDeviceService,
    @BluetoothClassic private val bluetoothClassicService: BluetoothService,
    @BluetoothLe private val bluetoothLeService: BluetoothService,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothRepository {

    private var connectionType: ConnectionType? = null

    private val updatedBluetoothDevices: MutableStateFlow<Map<String, BluetoothDevice>> =
        MutableStateFlow(emptyMap())

    private var favoriteDevice: MutableStateFlow<BluetoothDevice?> = MutableStateFlow(null)

    override fun getState(): StateFlow<BluetoothState> =
        listOf(
            bluetoothAdapterManager.getState(),
            bluetoothClassicService.getState(),
            bluetoothLeService.getState(),
        )
            .merge()
            .distinctUntilChanged()
            .onEach {
                connectionType = if (it is BluetoothState.On.Connected) {
                    it.device?.type?.connectionType
                } else {
                    null
                }

                if (it is BluetoothState.Off) {
                    stopScan()
                }
            }
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = bluetoothAdapterManager.initialState,
            )

    override fun getBluetoothDevices(): Flow<List<BluetoothDevice>> =
        getState().combine(bluetoothDeviceService.getScannedDevices()) { state, scannedBleDevices ->
            bluetoothDeviceService.getBondedDevices().first()
                .plus(scannedBleDevices)
                .distinctBy { it.address }
                .map { device ->
                    device.toBluetoothDevice(
                        state = state.toBluetoothDeviceState(device),
                    )
                }
        }
            .combine(updatedBluetoothDevices) { bluetoothDevices, updatedBluetoothDevices ->
                bluetoothDevices.map { bluetoothDevice ->
                    updatedBluetoothDevices[bluetoothDevice.address]?.let {
                        bluetoothDevice.copy(type = it.type)
                    } ?: bluetoothDevice
                }
            }
            .combine(favoriteDevice) { bluetoothDevices, favoriteDevice ->
                bluetoothDevices.map { bluetoothDevice ->
                    if (bluetoothDevice.address == favoriteDevice?.address) {
                        bluetoothDevice.copy(isFavorite = true)
                    } else {
                        bluetoothDevice
                    }
                }
            }
            .distinctUntilChanged()

    override fun updateBluetoothDevice(device: BluetoothDevice) {
        updatedBluetoothDevices.value += device.address to device
    }

    override fun getFavoriteBluetoothDevice(): StateFlow<BluetoothDevice?> = favoriteDevice

    override fun getScanState(): StateFlow<Boolean> = bluetoothDeviceService.getScanState()

    override suspend fun startScan(duration: Long) {
        bluetoothDeviceService.startScan(duration)
    }

    override fun stopScan() {
        bluetoothDeviceService.stopScan()
    }

    override fun enableAdapter() {
        bluetoothAdapterManager.enable()
    }

    override suspend fun connect(device: BluetoothDevice) {
        stopScan()
        disconnect()

        favoriteDevice.value = device

        when (device.type.connectionType) {
            is ConnectionType.Classic -> bluetoothClassicService.connect(device)
            is ConnectionType.Ble -> bluetoothLeService.connect(device)
        }
    }

    override fun disconnect(device: BluetoothDevice?) {
        when (device?.type?.connectionType) {
            is ConnectionType.Classic -> bluetoothClassicService.disconnect(device)
            is ConnectionType.Ble -> bluetoothLeService.disconnect(device)
            else -> {
                bluetoothClassicService.disconnect()
                bluetoothLeService.disconnect()
            }
        }
    }

    override fun getMessages(): Flow<List<Message>> = messagesDataSource.getMessages()

    @Throws(WriteMessageException::class)
    override fun writeMessage(message: Message) {
        when (connectionType) {
            is ConnectionType.Classic -> bluetoothClassicService.write(message)
            is ConnectionType.Ble -> bluetoothLeService.write(message)
            else -> {
                addMessage(message.copy(type = Message.Type.Error))
                throw WriteMessageException(message)
            }
        }
    }

    override fun addMessage(message: Message) {
        messagesDataSource.addMessage(message)
    }

    override fun deleteMessages() {
        messagesDataSource.deleteMessages()
    }
}