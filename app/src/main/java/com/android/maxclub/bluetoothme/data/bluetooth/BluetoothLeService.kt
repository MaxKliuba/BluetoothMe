package com.android.maxclub.bluetoothme.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.domain.bluetooth.model.*
import com.android.maxclub.bluetoothme.domain.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.messages.MessagesDataSource
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import android.bluetooth.BluetoothDevice as Device

class BluetoothLeService(
    private val context: Context,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothService {

    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter = manager.adapter

    private var bluetoothGatt: BluetoothGatt? = null

    private val bluetoothStateFlow: MutableStateFlow<BluetoothState> = MutableStateFlow(
        adapter.state.toBluetoothState()
    )

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            var isDisconnected = false
            if (gatt?.device != null) {
                val bluetoothDevice = gatt.device.toBluetoothDevice(
                    context = context,
                    state = newState.toBluetoothDeviceState(),
                    connectionType = ConnectionType.Ble,
                )

                when (newState) {
                    BluetoothProfile.STATE_CONNECTING -> {
                        bluetoothStateFlow.value = BluetoothState.TurnOn.Connecting(bluetoothDevice)
                    }
                    BluetoothProfile.STATE_CONNECTED -> {
                        bluetoothStateFlow.value = BluetoothState.TurnOn.Connected(bluetoothDevice)
                    }
                    BluetoothProfile.STATE_DISCONNECTING -> {
                        bluetoothStateFlow.value =
                            BluetoothState.TurnOn.Disconnecting(bluetoothDevice)
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        isDisconnected = true
                    }
                }
            } else {
                isDisconnected = true
            }

            if (isDisconnected) {
                bluetoothStateFlow.value = BluetoothState.TurnOn.Disconnected
                withCheckSelfBluetoothPermission(context) {
                    bluetoothGatt?.close()
                }
                bluetoothGatt = null
            }
        }

        // TODO
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
            adapter.getRemoteDevice(device.address)
        } catch (iae: IllegalArgumentException) {
            throw BluetoothConnectionException(device)
        }

        withCheckSelfBluetoothPermission(context) {
            bluetoothStateFlow.value = BluetoothState.TurnOn.Connecting(
                device.copy(state = BluetoothDeviceState.Connecting)
            )
            bluetoothGatt = remoteDevice.connectGatt(context, false, gattCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun disconnect(device: BluetoothDevice?) {
        withCheckSelfBluetoothPermission(context) {
            bluetoothGatt?.disconnect()
        }
    }

    override fun write(message: Message) {
        // TODO

        messagesDataSource.addMessage(message)
    }
}