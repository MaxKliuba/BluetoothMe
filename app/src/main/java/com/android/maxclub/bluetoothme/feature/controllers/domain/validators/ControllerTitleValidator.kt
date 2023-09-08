package com.android.maxclub.bluetoothme.feature.controllers.domain.validators

import javax.inject.Inject

class ControllerTitleValidator @Inject constructor() {
    operator fun invoke(title: String): Boolean {
        if (title.length > MAX_TITLE_LENGTH) return false

        return true
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 50
    }
}