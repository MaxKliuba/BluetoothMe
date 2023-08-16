package com.android.maxclub.bluetoothme.core.util

import java.util.UUID

fun String.toUuidOrElse(block: () -> Unit): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        block()
        null
    }