package com.android.maxclub.bluetoothme.feature.bluetooth.domain.validators

import javax.inject.Inject

class UuidValueValidator @Inject constructor() {
    operator fun invoke(uuidValue: String): Boolean {
        if (uuidValue.length > MAX_MESSAGE_LENGTH) return false

        return true
    }

    companion object {
        private const val MAX_MESSAGE_LENGTH = 100
    }
}