package com.android.maxclub.bluetoothme.data.repository

import android.content.Context
import com.android.maxclub.bluetoothme.domain.bluetooth.*
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

    private val deviceAddressesToConnectionTypes: MutableStateFlow<Map<String, ConnectionType>> =
        MutableStateFlow(emptyMap())

    override fun getState(): StateFlow<BluetoothState> =
        listOf(
            bluetoothAdapterStateObserver.getState(),
            bluetoothClassicService.getState(),
            bluetoothLeService.getState(),
        )
            .merge()
            .distinctUntilChanged()
            .onEach {
                connectionType = if (it is BluetoothState.TurnOn.Connected) {
                    it.device?.type?.connectionType
                } else {
                    null
                }
            }
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = bluetoothAdapterStateObserver.initialState,
            )

    override fun getBluetoothDevices(): Flow<List<BluetoothDevice>> =
        bluetoothDeviceService.getBondedDevices()
            .combine(bluetoothDeviceService.getScannedDevices()) { bondedDevices, scannedBleDevices ->
                bondedDevices.plus(scannedBleDevices)
                    .distinctBy { it.address }
            }
            .combine(getState()) { devices, state ->
                when (state) {
                    is BluetoothState.TurnOn -> {
                        devices.map { device ->
                            device.toBluetoothDevice(
                                context = context,
                                state = state.toBluetoothDeviceState(device),
                                connectionType = deviceAddressesToConnectionTypes.value[device.address]
                                    ?: ConnectionType.Classic,
                            )
                        }
                    }
                    else -> emptyList()
                }
            }
            .combine(deviceAddressesToConnectionTypes) { bluetoothDevices, _ ->
                bluetoothDevices
            }

    override fun updateBluetoothDevice(device: BluetoothDevice) {
        deviceAddressesToConnectionTypes.value += device.address to device.type.connectionType
    }

    override suspend fun startScan(duration: Long) {
        bluetoothDeviceService.startScan(duration)
    }

    override fun stopScan() {
        bluetoothDeviceService.stopScan()
    }

    override suspend fun connect(device: BluetoothDevice) {
        stopScan()
        disconnect()

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