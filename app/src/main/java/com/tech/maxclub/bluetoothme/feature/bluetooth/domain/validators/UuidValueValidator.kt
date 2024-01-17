package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.validators

import javax.inject.Inject

class UuidValueValidator @Inject constructor() {
    operator fun invoke(uuidValue: String): Boolean =
        uuidValue.length <= MAX_MESSAGE_LENGTH

    companion object {
        private const val MAX_MESSAGE_LENGTH = 36
    }
}