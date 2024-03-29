package com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import com.tech.maxclub.bluetoothme.core.exceptions.BluetoothConnectionException
import com.tech.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.tech.maxclub.bluetoothme.core.util.withCheckSelfBluetoothPermission
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toMessage
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothLeProfileManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.*
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessagesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothLeService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapterManager: BluetoothAdapterManager,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothService {

    private val adapter: BluetoothAdapter?
        get() = bluetoothAdapterManager.adapter

    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var profileManager: BluetoothLeProfileManager? = null
    private val readBuffer: MutableList<ByteArray> = mutableListOf()
    private val writeBuffer: MutableList<ByteArray> = mutableListOf()
    private var payloadSize: Int = DEFAULT_MTU - 3

    private val bluetoothStateFlow: MutableStateFlow<BluetoothState> = MutableStateFlow(
        bluetoothAdapterManager.initialState
    )

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            var isDisconnected = false
            if (gatt?.device != null) {
                val bluetoothDevice = gatt.device.toBluetoothDevice(
                    state = newState.toBluetoothDeviceState(),
                    connectionType = ConnectionType.Ble(),
                )

                when (newState) {
                    BluetoothProfile.STATE_CONNECTING -> {
                        bluetoothStateFlow.value = BluetoothState.On.Connecting(bluetoothDevice)
                    }

                    BluetoothProfile.STATE_CONNECTED -> {
                        if (!gatt.discoverServices()) {
                            isDisconnected = true
                        }
                    }

                    BluetoothProfile.STATE_DISCONNECTING -> {
                        bluetoothStateFlow.value = BluetoothState.On.Disconnecting(bluetoothDevice)
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        isDisconnected = true
                    }
                }
            } else {
                isDisconnected = true
            }

            if (isDisconnected) {
                bluetoothStateFlow.value = BluetoothState.On.Disconnected
                clearConnectionObjects()
                closeGatt()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.connectCharacteristics1() ?: return disconnect()
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                payloadSize = mtu - 3
            }
            gatt?.connectCharacteristics3() ?: return disconnect()
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)

            if (gatt == null || descriptor == null) {
                return disconnect()
            }

            if (descriptor.characteristic == profileManager?.readCharacteristic) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    return disconnect()
                } else {
                    bluetoothStateFlow.value = BluetoothState.On.Connected(
                        gatt.device.toBluetoothDevice(
                            state = BluetoothDeviceState.Connected,
                            connectionType = ConnectionType.Ble(),
                        )
                    )
                }
            }
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)

            // reading
            if (characteristic == profileManager?.readCharacteristic) {
                synchronized(readBuffer) {
                    val data = characteristic.value
                    val lastIndex = data.lastIndexOf(Message.MESSAGE_TERMINATOR.toByte())

                    if (lastIndex > 0) {
                        val bytes =
                            (readBuffer.reduceOrNull { acc, bytes -> acc + bytes } ?: byteArrayOf())
                                .plus(data.copyOf(lastIndex))

                        String(bytes)
                            .split(Message.MESSAGE_TERMINATOR)
                            .filterNot { it.isEmpty() }
                            .forEach { messageString ->
                                messagesDataSource.addMessage(messageString.toMessage(Message.Type.Input))
                            }

                        readBuffer.clear()
                        if (lastIndex < data.size) {
                            readBuffer.add(data.copyOfRange(lastIndex, data.size))
                        }
                    } else {
                        readBuffer.add(data)
                    }

                    return@synchronized
                }
            }
        }

        @SuppressLint("MissingPermission")
        @Suppress("DEPRECATION")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            // finish writing
            if (characteristic == profileManager?.writeCharacteristic) {
                synchronized(writeBuffer) {
                    writeBuffer.removeFirstOrNull()?.let { data ->
                        profileManager?.writeCharacteristic?.value = data
                        gatt?.writeCharacteristic(profileManager?.writeCharacteristic)
                    }
                }
            }
        }
    }

    override val initialState: BluetoothState
        get() = bluetoothStateFlow.value

    override fun getState(): Flow<BluetoothState> = bluetoothStateFlow

    @SuppressLint("MissingPermission")
    @Throws(BluetoothConnectionException::class)
    override suspend fun connect(device: BluetoothDevice) {
        disconnect()

        if (device.type.connectionType !is ConnectionType.Ble) {
            throw BluetoothConnectionException(device)
        }

        val remoteDevice = try {
            adapter?.getRemoteDevice(device.address)
        } catch (iae: IllegalArgumentException) {
            throw BluetoothConnectionException(device)
        }

        withCheckSelfBluetoothPermission(context) {
            bluetoothGatt = remoteDevice?.connectGatt(context, false, gattCallback)
        }

        if (bluetoothGatt == null) {
            bluetoothStateFlow.value = BluetoothState.On.Disconnected
            throw BluetoothConnectionException(device)
        }

        bluetoothDevice = device
        bluetoothStateFlow.value = BluetoothState.On.Connecting(
            device.copy(state = BluetoothDeviceState.Connecting)
        )
    }

    @SuppressLint("MissingPermission")
    override fun disconnect(device: BluetoothDevice?) {
        bluetoothStateFlow.value = BluetoothState.On.Disconnected

        clearConnectionObjects()
        withCheckSelfBluetoothPermission(context) {
            bluetoothGatt?.disconnect()
        }
    }

    private fun clearConnectionObjects() {
        synchronized(readBuffer) {
            readBuffer.clear()
        }

        synchronized(writeBuffer) {
            writeBuffer.clear()
        }

        bluetoothDevice = null
        profileManager = null
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override fun write(message: Message) {
        if (profileManager?.writeCharacteristic == null) {
            messagesDataSource.addMessage(message.copy(type = Message.Type.Error))
            throw WriteMessageException(message)
        }

        // start writing
        synchronized(writeBuffer) {
            writeBuffer.addAll(
                message.toByteArray()
                    .toList()
                    .chunked(payloadSize)
                    .map { it.toByteArray() }
            )

            writeBuffer.removeFirstOrNull()?.let { data ->
                profileManager?.writeCharacteristic?.value = data
                withCheckSelfBluetoothPermission(context) {
                    if (bluetoothGatt?.writeCharacteristic(profileManager?.writeCharacteristic) != true) {
                        messagesDataSource.addMessage(message.copy(type = Message.Type.Error))
                        throw WriteMessageException(message)
                    } else {
                        messagesDataSource.addMessage(message.copy(type = Message.Type.Output))
                    }
                }
            }
        }
    }

    private fun BluetoothGatt.createProfileManager(profile: BluetoothLeProfile): BluetoothLeProfileManager? =
        services.firstNotNullOfOrNull { service ->
            when (profile) {
                is BluetoothLeProfile.Default -> CC254XProfileManager.fromService(service)
                    ?: RN4870ProfileManager.fromService(service)
                    ?: NRFProfileManager.fromService(service)

                is BluetoothLeProfile.Custom -> CustomProfileManager.fromService(service, profile)
            }
        }

    private fun BluetoothGatt.connectCharacteristics1() {
        profileManager =
            (bluetoothDevice?.type?.connectionType as? ConnectionType.Ble)?.profile?.let { profile ->
                this.createProfileManager(profile)
            }

        profileManager?.let {
            if (it.connectCharacteristics()) {
                this.connectCharacteristics2()
            } else {
                null
            }
        } ?: return this@BluetoothLeService.disconnect()
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothGatt.connectCharacteristics2() {
        if (!this.requestMtu(MAX_MTU)) {
            return this@BluetoothLeService.disconnect()
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun BluetoothGatt.connectCharacteristics3() {
        val writeProperties = profileManager?.writeCharacteristic?.properties

        if (writeProperties == null || (writeProperties and (BluetoothGattCharacteristic.PROPERTY_WRITE +
                    BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0
        ) {
            return this@BluetoothLeService.disconnect()
        }

        if (!this.setCharacteristicNotification(profileManager?.readCharacteristic, true)) {
            return this@BluetoothLeService.disconnect()
        }

        val readDescriptor = profileManager?.readCharacteristic?.getDescriptor(BLUETOOTH_LE_CCCD)
            ?: return this@BluetoothLeService.disconnect()
        val readProperties = profileManager?.readCharacteristic?.properties

        if (readProperties != null && (readProperties and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
            readDescriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        } else if (readProperties != null && (readProperties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            readDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        } else {
            return this@BluetoothLeService.disconnect()
        }

        if (!this.writeDescriptor(readDescriptor)) {
            return this@BluetoothLeService.disconnect()
        }
    }

    companion object {
        private val BLUETOOTH_LE_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        // BLE standard does not limit, some BLE 4.2 devices support 251, various source say that Android has max 512
        private const val MAX_MTU = 512
        private const val DEFAULT_MTU = 23
    }
}