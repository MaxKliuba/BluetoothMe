package com.android.maxclub.bluetoothme.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothAdapterManager
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.bluetooth.model.toBluetoothState
import com.android.maxclub.bluetoothme.domain.exceptions.EnableBluetoothAdapterException
import com.android.maxclub.bluetoothme.util.withCheckSelfBluetoothPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BluetoothAdapterManagerImpl(private val context: Context) : BluetoothAdapterManager {

    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)

    override val adapter: BluetoothAdapter = manager.adapter

    override val initialState: BluetoothState
        get() = adapter.state.toBluetoothState()

    override fun getState(): Flow<BluetoothState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    trySend(
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                            .toBluetoothState()
                    )
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        context.registerReceiver(receiver, intentFilter)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    @Throws(EnableBluetoothAdapterException::class)
    override fun enable() {
        if (!adapter.isEnabled) {
            withCheckSelfBluetoothPermission(context) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    adapter.enable()
                } else {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    throw EnableBluetoothAdapterException(intent)
                }
            }
        }
    }
}