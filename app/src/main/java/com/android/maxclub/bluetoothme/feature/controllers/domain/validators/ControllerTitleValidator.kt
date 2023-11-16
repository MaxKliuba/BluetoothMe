package com.android.maxclub.bluetoothme.feature.controllers.domain.validators

import javax.inject.Inject

class ControllerTitleValidator @Inject constructor() {
    operator fun invoke(value: String): Boolean =
        value.length <= MAX_VALUE_LENGTH

    companion object {
        private const val MAX_VALUE_LENGTH = 50
    }
}