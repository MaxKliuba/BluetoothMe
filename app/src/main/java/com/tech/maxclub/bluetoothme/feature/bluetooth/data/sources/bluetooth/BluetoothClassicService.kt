package com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tech.maxclub.bluetoothme.core.exceptions.BluetoothConnectionException
import com.tech.maxclub.bluetoothme.core.exceptions.WriteMessageException
import com.tech.maxclub.bluetoothme.core.util.getParcelable
import com.tech.maxclub.bluetoothme.core.util.withCheckSelfBluetoothPermission
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothDeviceState
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toMessage
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothService
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.*
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothDevice
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.MessagesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import android.bluetooth.BluetoothDevice as Device

@Singleton
class BluetoothClassicService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapterManager: BluetoothAdapterManager,
    private val messagesDataSource: MessagesDataSource,
) : BluetoothService {

    private val adapter: BluetoothAdapter?
        get() = bluetoothAdapterManager.adapter

    private var socket: BluetoothSocket? = null
    private val remoteDevice: Device?
        get() = socket?.remoteDevice

    private val bluetoothStateFlow: MutableStateFlow<BluetoothState> = MutableStateFlow(
        bluetoothAdapterManager.initialState
    )

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private val bluetoothStateCallbackFlow: Flow<BluetoothState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Device.ACTION_ACL_CONNECTED,
                    Device.ACTION_ACL_DISCONNECTED,
                    Device.ACTION_ACL_DISCONNECT_REQUESTED -> {
                        intent.getParcelable<Device>(Device.EXTRA_DEVICE)?.let { device ->
                            if (device.address == remoteDevice?.address) {
                                val bluetoothDevice = device.toBluetoothDevice(
                                    state = intent.action.toBluetoothDeviceState(),
                                    connectionType = ConnectionType.Classic,
                                )

                                trySend(
                                    when (intent.action) {
                                        Device.ACTION_ACL_CONNECTED ->
                                            BluetoothState.On.Connected(bluetoothDevice)

                                        Device.ACTION_ACL_DISCONNECT_REQUESTED ->
                                            BluetoothState.On.Disconnecting(bluetoothDevice)

                                        else -> BluetoothState.On.Disconnected
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

    override val initialState: BluetoothState
        get() = bluetoothStateFlow.value

    override fun getState(): Flow<BluetoothState> =
        listOf(bluetoothStateFlow, bluetoothStateCallbackFlow)
            .merge()

    @SuppressLint("MissingPermission")
    @Throws(BluetoothConnectionException::class)
    override suspend fun connect(device: BluetoothDevice) {
        disconnect()

        if (device.type.connectionType !is ConnectionType.Classic) {
            throw BluetoothConnectionException(device)
        }

        withContext(Dispatchers.IO) {
            try {
                socket = withCheckSelfBluetoothPermission(context) {
                    adapter?.getRemoteDevice(device.address)
                        ?.createRfcommSocketToServiceRecord(uuid)
                }
                bluetoothStateFlow.value = BluetoothState.On.Connecting(
                    device.copy(state = BluetoothDeviceState.Connecting)
                )

                socket?.connect()
                bluetoothStateFlow.value = BluetoothState.On.Connected(
                    device.copy(state = BluetoothDeviceState.Connected)
                )

                startReading()
            } catch (iae: IllegalArgumentException) {
                disconnect(device)
                throw BluetoothConnectionException(device)
            } catch (ioe: IOException) {
                disconnect(device)
                throw BluetoothConnectionException(device)
            }
        }
    }

    override fun disconnect(device: BluetoothDevice?) {
        try {
            socket?.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } finally {
            socket = null
            bluetoothStateFlow.value = BluetoothState.On.Disconnected
        }
    }

    @Throws(WriteMessageException::class)
    override fun write(message: Message) {
        if (socket == null) {
            messagesDataSource.addMessage(message.copy(type = Message.Type.Error))
            throw WriteMessageException(message)
        }

        try {
            socket?.outputStream?.write(message.toByteArray())
            messagesDataSource.addMessage(message.copy(type = Message.Type.Output))
        } catch (ioe: IOException) {
            messagesDataSource.addMessage(message.copy(type = Message.Type.Error))
            throw WriteMessageException(message)
        }
    }

    private suspend fun startReading() =
        withContext(Dispatchers.IO) {
            while (socket?.isConnected == true) {
                socket?.inputStream?.apply {
                    try {
                        val message = StringBuilder()

                        while (true) {
                            val currentChar = read()
                            if (currentChar == -1 || currentChar.toChar() == Message.MESSAGE_TERMINATOR) {
                                break
                            }
                            message.append(currentChar.toChar())
                        }

                        messagesDataSource.addMessage(
                            message.toString().toMessage(Message.Type.Input)
                        )
                    } catch (ioe: IOException) {
                        ioe.printStackTrace()
                    }
                }
            }
        }

    companion object {
        private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}