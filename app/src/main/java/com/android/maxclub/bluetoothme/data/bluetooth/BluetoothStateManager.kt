package com.android.maxclub.bluetoothme.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.android.maxclub.bluetoothme.domain.model.BluetoothState
import com.android.maxclub.bluetoothme.domain.model.BondedDeviceState
import com.android.maxclub.bluetoothme.domain.model.toBluetoothState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@Suppress("DEPRECATION")
class BluetoothStateManager(
    private val context: Context,
    initialState: BluetoothState,
) {
    private var stateFlow = MutableStateFlow(initialState)
    private val callbackStateFlow: Flow<BluetoothState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        stateFlow.value = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF
                        ).toBluetoothState()
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED, BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        (stateFlow.value as? BluetoothState.TurnOn)?.device?.let { bondedDevice ->
                            val bluetoothDevice =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    intent.getParcelableExtra(
                                        BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java
                                    )
                                } else {
                                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                }

                            bluetoothDevice?.let { device ->
                                if (device.address == bondedDevice.address) {
                                    stateFlow.value = when (intent.action) {
                                        BluetoothDevice.ACTION_ACL_CONNECTED ->
                                            BluetoothState.TurnOn.Connected(
                                                bondedDevice.copy(state = BondedDeviceState.Connected)
                                            )
                                        else -> BluetoothState.TurnOn.Disconnected
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        context.registerReceiver(receiver, intentFilter)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    fun getState(): StateFlow<BluetoothState> = listOf(stateFlow, callbackStateFlow)
        .merge()
        .distinctUntilChanged()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = stateFlow.value,
        )

    fun setState(state: BluetoothState) {
        stateFlow.value = state
    }
}