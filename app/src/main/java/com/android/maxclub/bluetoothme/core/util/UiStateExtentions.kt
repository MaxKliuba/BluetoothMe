package com.android.maxclub.bluetoothme.core.util

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

inline fun <T> MutableState<T>.update(block: (T) -> T) {
    this.value = block(this.value)
}

fun <T> Channel<T>.sendIn(element: T, scope: CoroutineScope) {
    scope.launch { this@sendIn.send(element) }
}