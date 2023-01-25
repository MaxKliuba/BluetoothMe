package com.android.maxclub.bluetoothme.domain.bluetooth

import android.bluetooth.BluetoothAdapter

interface BluetoothAdapterManager : BluetoothStateObserver {

    val adapter: BluetoothAdapter

    fun enable()
}