package com.android.maxclub.bluetoothme.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.maxclub.bluetoothme.domain.exception.MissingBluetoothPermissionException
import com.android.maxclub.bluetoothme.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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

    @Suppress("DEPRECATION")
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

    fun connect(device: BondedDevice) {
        stateManager.setState(BluetoothState.TurnOn.Connecting(device))

        when (device.type.connectionType) {
            ConnectionType.CLASSIC -> {

            }
            ConnectionType.BLE -> {
                // TODO
            }
        }
    }

    fun disconnect(device: BondedDevice) {
        stateManager.setState(BluetoothState.TurnOn.Disconnecting(device))
    }
}