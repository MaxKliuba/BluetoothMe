package com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions

import android.content.Intent

data class EnableBluetoothAdapterException(val intent: Intent) : Exception()