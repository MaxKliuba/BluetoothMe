package com.android.maxclub.bluetoothme.feature.controllers.domain.validators

import javax.inject.Inject

class WidgetSliderRangeValidator @Inject constructor() {
    operator fun invoke(minValue: Int, maxValue: Int, step: Int): Boolean =
        step >= 1 && maxValue - minValue >= step
}