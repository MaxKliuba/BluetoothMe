package com.android.maxclub.bluetoothme.core.exceptions

import android.content.Intent

data class EnableBluetoothAdapterException(val intent: Intent) : Exception()