package com.android.maxclub.bluetoothme.domain.exceptions

import android.content.Intent

data class EnableBluetoothAdapterException(val intent: Intent) : Exception()