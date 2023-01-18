package com.android.maxclub.bluetoothme.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothService
import com.android.maxclub.bluetoothme.domain.bluetooth.model.*
import com.android.maxclub.bluetoothme.domain.exceptions.BluetoothConnectionException
import com.android.maxclub.bluetoothme.domain.messages.Message
import com.android.maxclub.bluetoothme.domain.messages.MessagesDataSource
import com.android.maxclub.bluetoothme.util.getParcelable
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import android.bluetooth.BluetoothDevice as Device

class BluetoothLeService(
    private val context: Context,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothService {

    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter = manager.adapter

    private var bluetoothDevice: BluetoothDevice? = null

    private val bluetoothStateFlow: MutableStateFlow<BluetoothState> = MutableStateFlow(
        adapter.state.toBluetoothState()
    )

    private val bluetoothStateCallbackFlow: Flow<BluetoothState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Device.ACTION_ACL_CONNECTED,
                    Device.ACTION_ACL_DISCONNECTED,
                    Device.ACTION_ACL_DISCONNECT_REQUESTED -> {
                        intent.getParcelable<Device>(Device.EXTRA_DEVICE)?.let { device ->
                            if (device.address == bluetoothDevice?.address) {
                                val bluetoothDevice = device.toBluetoothDevice(
                                    context = this@BluetoothLeService.context,
                                    state = BluetoothDeviceState.Connected,
                                    connectionType = ConnectionType.Ble,
                                )

                                trySend(
                                    when (intent.action) {
                                        Device.ACTION_ACL_CONNECTED ->
                                            BluetoothState.TurnOn.Connected(bluetoothDevice)
                                        Device.ACTION_ACL_DISCONNECT_REQUESTED ->
                                            BluetoothState.TurnOn.Disconnecting(bluetoothDevice)
                                        else -> BluetoothState.TurnOn.Disconnected
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Device.ACTION_ACL_CONNECTED)
            addAction(Device.ACTION_ACL_DISCONNECTED)
            addAction(Device.ACTION_ACL_DISCONNECT_REQUESTED)
        }
        context.registerReceiver(receiver, intentFilter)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
        }
    }

    override val initialState: BluetoothState
        get() = bluetoothStateFlow.value

    override fun getState(): Flow<BluetoothState> =
        listOf(bluetoothStateFlow, bluetoothStateCallbackFlow)
            .merge()
            .distinctUntilChanged()

    @SuppressLint("MissingPermission")
    override fun getDevices(): Flow<List<Device>> = flow {
        withCheckSelfBluetoothPermission(context) {
            emit(adapter.bondedDevices.toList().filterNot { it.type == Device.DEVICE_TYPE_CLASSIC })

            // TODO Scan BLE
        }
    }

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
            // TODO
//            bluetoothStateFlow.value = BluetoothState.TurnOn.Disconnected
            throw BluetoothConnectionException(device)
        }

        withCheckSelfBluetoothPermission(context) {
            remoteDevice.connectGatt(context, false, gattCallback)
        }
    }

    override fun disconnect(device: BluetoothDevice?) {
        // TODO
    }

    override fun write(message: Message) {
        // TODO

        messagesDataSource.addMessage(message)
    }
}