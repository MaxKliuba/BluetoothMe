package com.tech.maxclub.bluetoothme

import com.tech.maxclub.bluetoothme.feature.controllers.domain.validators.WidgetSliderRangeValidator
import org.junit.Assert.*
import org.junit.Test

class WidgetSliderRangeValidatorTest {

    @Test
    fun validate_ValidRange_ReturnsTrue() {
        val validator = WidgetSliderRangeValidator()

        val isValid = validator(minValue = 0, maxValue = 10, step = 1)

        assertTrue(isValid)
    }

    @Test
    fun validate_MinValueIsGreaterThanMaxValue_ReturnsFalse() {
        val validator = WidgetSliderRangeValidator()

        val isValid = validator(minValue = 10, maxValue = 0, step = 1)

        assertFalse(isValid)
    }

    @Test
    fun validate_StepIsGreaterThanMinMaxRange_ReturnsFalse() {
        val validator = WidgetSliderRangeValidator()

        val isValid = validator(minValue = 0, maxValue = 10, step = 11)

        assertFalse(isValid)
    }

    @Test
    fun validate_StepIsLessThanOne_ReturnsFalse() {
        val validator = WidgetSliderRangeValidator()

        val isValid = validator(minValue = 0, maxValue = 10, step = 0)

        assertFalse(isValid)
    }

    @Test
    fun validate_MinValueIsGreaterThanMaxValueAndStepIsLessThanOne_ReturnsFalse() {
        val validator = WidgetSliderRangeValidator()

        val isValid = validator(minValue = 10, maxValue = 0, step = -11)

        assertFalse(isValid)
    }
}
