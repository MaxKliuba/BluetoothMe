package com.android.maxclub.bluetoothme.core.util

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

inline fun <T> MutableState<T>.update(block: (T) -> T) {
    this.value = block(this.value)
}

fun <T> Channel<T>.sendIn(element: T, scope: CoroutineScope) {
    scope.launch { this@sendIn.send(element) }
}

fun <T> CoroutineScope.debounce(
    timeoutMillis: Long = 300L,
    block: suspend (T) -> Unit
): (T) -> Unit {
    var job: Job? = null

    return { param: T ->
        job?.cancel() // Cancel the previous debounce job

        job = launch {
            delay(timeoutMillis)
            block(param)
        }
    }
}