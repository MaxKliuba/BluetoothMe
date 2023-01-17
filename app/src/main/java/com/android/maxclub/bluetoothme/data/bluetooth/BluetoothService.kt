package com.android.maxclub.bluetoothme.data.bluetooth

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.maxclub.bluetoothme.domain.exception.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.domain.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.model.BondedDevice
import com.android.maxclub.bluetoothme.domain.model.ConnectionType
import com.android.maxclub.bluetoothme.domain.model.toBluetoothState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter = manager.adapter

    private val stateManager: BluetoothStateManager = BluetoothStateManager(
        context = context,
        initialState = adapter.state.toBluetoothState(),
    )

    private lateinit var socket: BluetoothSocket

    fun getState(): StateFlow<BluetoothState> = stateManager.getState()

    @Throws(MissingBluetoothPermissionException::class)
    fun getBondedDevices(): Flow<List<BluetoothDevice>> = flow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw MissingBluetoothPermissionException(Manifest.permission.BLUETOOTH_CONNECT)
        }
        emit(adapter.bondedDevices.toList())
    }

    @Throws(MissingBluetoothPermissionException::class, IOException::class)
    suspend fun connect(device: BondedDevice) {
        cancelDiscovery()

        disconnectAll()
        stateManager.setState(BluetoothState.TurnOn.Connecting(device))

        val bluetoothDevice = adapter.getRemoteDevice(device.address)

        when (device.type.connectionType) {
            ConnectionType.CLASSIC -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    throw MissingBluetoothPermissionException(Manifest.permission.BLUETOOTH_CONNECT)
                }

                try {
                    socket = bluetoothDevice.createRfcommSocketToServiceRecord(classicBluetoothUuid)

                    withContext(Dispatchers.IO) {
                        try {
                            socket.connect()
                        } catch (ioe: IOException) {
                            stateManager.setState(BluetoothState.TurnOn.Disconnected)
                            //throw ioe
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    // TODO
                }
            }
            ConnectionType.BLE -> {
                bluetoothDevice.connectGatt(context, false, gattCallback)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
        }
    }

    fun disconnect(device: BondedDevice) {
        stateManager.setState(BluetoothState.TurnOn.Disconnecting(device))

        disconnectAll()
    }

    fun disconnectAll() {
        try {
            if (::socket.isInitialized && socket.isConnected) {
                socket.close()
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } finally {
            stateManager.setState(BluetoothState.TurnOn.Disconnected)
        }
    }

    fun cancelDiscovery() {
//        adapter.cancelDiscovery()
        // TODO
    }

    companion object {
        private val classicBluetoothUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}