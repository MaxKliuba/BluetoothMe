package com.android.maxclub.bluetoothme.feature.controllers.domain.validators

import javax.inject.Inject

class MessageTagValidator @Inject constructor() {
    operator fun invoke(value: String): Boolean {
        if (value.length > MAX_VALUE_LENGTH) return false

        return true
    }

    companion object {
        private const val MAX_VALUE_LENGTH = 20
    }
}