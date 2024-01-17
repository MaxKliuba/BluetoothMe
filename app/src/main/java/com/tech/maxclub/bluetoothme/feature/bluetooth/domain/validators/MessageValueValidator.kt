package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.validators

import javax.inject.Inject

class MessageValueValidator @Inject constructor() {
    operator fun invoke(messageValue: String): Boolean =
        messageValue.length <= MAX_MESSAGE_LENGTH

    companion object {
        private const val MAX_MESSAGE_LENGTH = 100
    }
}