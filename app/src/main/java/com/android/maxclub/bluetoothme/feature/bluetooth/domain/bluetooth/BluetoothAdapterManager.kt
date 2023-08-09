package com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth

import android.bluetooth.BluetoothAdapter

interface BluetoothAdapterManager : BluetoothStateObserver {

    val adapter: BluetoothAdapter

    fun enable()
}