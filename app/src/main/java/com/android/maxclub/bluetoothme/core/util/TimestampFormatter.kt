package com.android.maxclub.bluetoothme.core.util

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun Long.format(is24HourFormat: Boolean): String {
    val pattern = if (is24HourFormat) "HH:mm" else "hh:mm"

    return SimpleDateFormat(pattern, Locale.getDefault())
        .format(this)
}

fun Long.format(context: Context): String =
    this.format(DateFormat.is24HourFormat(context))