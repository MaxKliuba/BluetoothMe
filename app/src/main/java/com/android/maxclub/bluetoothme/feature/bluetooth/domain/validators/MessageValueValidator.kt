package com.android.maxclub.bluetoothme.feature.bluetooth.domain.validators

import javax.inject.Inject

class MessageValueValidator @Inject constructor() {
    operator fun invoke(messageValue: String): Boolean {
        if (messageValue.length > MAX_MESSAGE_LENGTH) return false

        return true
    }

    companion object {
        private const val MAX_MESSAGE_LENGTH = 100
    }
}