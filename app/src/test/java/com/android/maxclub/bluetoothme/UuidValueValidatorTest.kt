package com.android.maxclub.bluetoothme

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.validators.UuidValueValidator
import org.junit.Assert.*
import org.junit.Test

class UuidValueValidatorTest {

    @Test
    fun validate_ValidValue_ReturnsTrue() {
        val uuidValueValidator = UuidValueValidator()

        val isValid = uuidValueValidator("00001101-0000-1000-8000-00805F9B34FB")

        assertTrue(isValid)
    }

    @Test
    fun validate_TooLongValue_ReturnsFalse() {
        val uuidValueValidator = UuidValueValidator()

        val isValid = uuidValueValidator("00001101-0000-1000-8000-00805F9B34FB0")

        assertFalse(isValid)
    }
}