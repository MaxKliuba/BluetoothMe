package com.android.maxclub.bluetoothme.core.util

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

inline fun <T> MutableState<T>.update(block: (T) -> T) {
    this.value = block(this.value)
}

inline fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
    this.value = block(this.value)
}

fun <T> Channel<T>.sendIn(element: T, scope: CoroutineScope) {
    scope.launch { this@sendIn.send(element) }
}

fun <T, V> CoroutineScope.debounce(
    timeoutMillis: Long = 300L,
    block: suspend (T, V) -> Unit
): (T, V) -> Unit {
    var job: Job? = null

    return { param1: T, param2: V ->
        job?.cancel() // Cancel the previous debounce job

        job = launch {
            delay(timeoutMillis)
            block(param1, param2)
        }
    }
}