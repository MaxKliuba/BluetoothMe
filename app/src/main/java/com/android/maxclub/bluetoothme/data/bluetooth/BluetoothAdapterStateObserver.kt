package com.android.maxclub.bluetoothme.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.android.maxclub.bluetoothme.domain.bluetooth.BluetoothStateObserver
import com.android.maxclub.bluetoothme.domain.bluetooth.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.bluetooth.model.toBluetoothState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BluetoothAdapterStateObserver(private val context: Context) : BluetoothStateObserver {

    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter = manager.adapter

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
}