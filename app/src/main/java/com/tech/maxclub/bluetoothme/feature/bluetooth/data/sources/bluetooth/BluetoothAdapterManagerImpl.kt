package com.tech.maxclub.bluetoothme.feature.bluetooth.data.sources.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tech.maxclub.bluetoothme.core.exceptions.EnableBluetoothAdapterException
import com.tech.maxclub.bluetoothme.core.util.withCheckSelfBluetoothPermission
import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toBluetoothState
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.BluetoothAdapterManager
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
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

    override val adapter: BluetoothAdapter?
        get() = context.getSystemService(BluetoothManager::class.java)?.adapter

    override val initialState: BluetoothState
        get() = adapter?.state?.toBluetoothState() ?: BluetoothState.Off

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

    @Throws(EnableBluetoothAdapterException::class)
    override fun enable() {
        if (adapter?.isEnabled == false) {
            withCheckSelfBluetoothPermission(context) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                throw EnableBluetoothAdapterException(intent)
            }
        }
    }
}