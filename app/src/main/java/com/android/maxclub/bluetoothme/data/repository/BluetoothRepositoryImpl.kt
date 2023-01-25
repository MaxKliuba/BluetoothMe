package com.android.maxclub.bluetoothme.data.repository

import android.content.Context
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothDeviceService
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothStateObserver
import com.android.maxclub.bluetoothme.domain.bluetooth.model.*
import com.android.maxclub.bluetoothme.domain.exceptions.WriteMessageException
import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.messages.MessagesDataSource
import com.android.maxclub.bluetoothme.domain.repository.BluetoothRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapterStateObserver: BluetoothStateObserver,
    private val bluetoothDeviceService: BluetoothDeviceService,
    private val bluetoothClassicService: BluetoothService,
    private val bluetoothLeService: BluetoothService,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothRepository {

    private var connectionType: ConnectionType? = null

    private val updatedBluetoothDevices: MutableStateFlow<Map<String, BluetoothDevice>> =
        MutableStateFlow(emptyMap())

    private var favoriteDevice: MutableStateFlow<BluetoothDevice?> = MutableStateFlow(null)

    override fun getState(): StateFlow<BluetoothState> =
        listOf(
            bluetoothAdapterStateObserver.getState(),
            bluetoothClassicService.getState(),
            bluetoothLeService.getState(),
        )
            .merge()
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
                initialValue = bluetoothAdapterStateObserver.initialState,
            )

    override fun getBluetoothDevices(): Flow<List<BluetoothDevice>> =
        getState().combine(bluetoothDeviceService.getScannedDevices()) { state, scannedBleDevices ->
            val devices = bluetoothDeviceService.getBondedDevices().first()
                .plus(scannedBleDevices)
                .distinctBy { it.address }

            when (state) {
                is BluetoothState.On -> {
                    devices.map { device ->
                        device.toBluetoothDevice(
                            context = context,
                            state = state.toBluetoothDeviceState(device),
                        )
                    }
                }
                else -> emptyList()
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

    override suspend fun connect(device: BluetoothDevice) {
        stopScan()
        disconnect()

        favoriteDevice.value = device

        when (device.type.connectionType) {
            ConnectionType.Classic -> bluetoothClassicService.connect(device)
            ConnectionType.Ble -> bluetoothLeService.connect(device)
        }
    }

    override fun disconnect(device: BluetoothDevice?) {
        when (device?.type?.connectionType) {
            ConnectionType.Classic -> bluetoothClassicService.disconnect(device)
            ConnectionType.Ble -> bluetoothLeService.disconnect(device)
            else -> {
                bluetoothClassicService.disconnect()
                bluetoothLeService.disconnect()
            }
        }
    }

    @Throws(WriteMessageException::class)
    override fun writeMessage(message: Message) {
        when (connectionType) {
            ConnectionType.Classic -> bluetoothClassicService.write(message)
            ConnectionType.Ble -> bluetoothLeService.write(message)
            else -> {
                messagesDataSource.addMessage(message.copy(type = Message.Type.Error))
                throw WriteMessageException(message)
            }
        }
    }

    override fun getMessages(): Flow<List<Message>> = messagesDataSource.getMessages()
}