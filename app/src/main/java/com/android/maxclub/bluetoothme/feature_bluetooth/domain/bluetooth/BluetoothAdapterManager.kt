package com.android.maxclub.bluetoothme.feature_bluetooth.domain.bluetooth

import android.bluetooth.BluetoothAdapter

interface BluetoothAdapterManager : BluetoothStateObserver {

    val adapter: BluetoothAdapter

    fun enable()
}