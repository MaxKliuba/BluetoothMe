package com.tech.maxclub.bluetoothme.core.exceptions

import android.content.Intent

data class EnableLocationException(val intent: Intent) : Exception()