package com.android.maxclub.bluetoothme.presentation.util

fun Int.toStringOrEmpty(): String = if (this > 0) this.toString() else ""