package com.android.maxclub.bluetoothme.feature_bluetooth.data.sources.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.android.maxclub.bluetoothme.feature_bluetooth.data.mappers.toBluetoothState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions.EnableBluetoothAdapterException
import com.android.maxclub.bluetoothme.feature_bluetooth.util.withCheckSelfBluetoothPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothAdapterManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BluetoothAdapterManager {

    override val adapter: BluetoothAdapter
        get() = context.getSystemService(BluetoothManager::class.java).adapter

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
            enable()
        }
    }

    @Throws(EnableBluetoothAdapterException::class)
    override fun enable() {
        if (!adapter.isEnabled) {
            withCheckSelfBluetoothPermission(context) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                throw EnableBluetoothAdapterException(intent)
            }
        }
    }
}