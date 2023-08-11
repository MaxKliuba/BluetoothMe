package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.connection.util

import java.util.UUID

fun String.toUuidOrElse(block: () -> Unit): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        block()
        null
    }