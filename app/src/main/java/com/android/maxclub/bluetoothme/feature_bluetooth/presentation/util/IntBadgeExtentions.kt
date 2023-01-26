package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.util

fun Int.toStringOrEmpty(): String = if (this > 0) this.toString() else ""