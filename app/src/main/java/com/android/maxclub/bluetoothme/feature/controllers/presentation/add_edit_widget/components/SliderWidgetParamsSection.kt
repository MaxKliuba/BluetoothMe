package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidgetParamsSection(
    sliderWidget: Widget.Slider,
    onRangeValueChange: (Widget.Slider, Int, Int) -> Unit,
    onStepValueChange: (Widget.Slider, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var rangeSliderPosition by remember(sliderWidget.minValue, sliderWidget.maxValue) {
        mutableStateOf(sliderWidget.minValue.toFloat()..sliderWidget.maxValue.toFloat())
    }
    val onRangeSliderValueChange: (ClosedFloatingPointRange<Float>) -> Unit = {
        rangeSliderPosition = it
        onRangeValueChange(
            sliderWidget,
            it.start.roundToInt(),
            it.endInclusive.roundToInt()
        )
    }

    var stepSliderPosition by remember(sliderWidget.step) {
        mutableFloatStateOf(sliderWidget.step.toFloat())
    }
    val onStepSliderValueChange: (Float) -> Unit = {
        stepSliderPosition = it
        onStepValueChange(sliderWidget, it.roundToInt())
    }

    Column(modifier = modifier) {
        Text(text = stringResource(R.string.slider_range_label))
        Row {
            RangeSlider(
                value = rangeSliderPosition,
                onValueChange = onRangeSliderValueChange,
                valueRange = Widget.Slider.MIN_VALUE.toFloat()..Widget.Slider.MAX_VALUE.toFloat(),
                steps = Widget.Slider.MAX_VALUE - Widget.Slider.MIN_VALUE - 1,
            )
        }
        Row {
            SliderIncDecButtons(
                value = rangeSliderPosition.start.roundToInt(),
                step = 1,
                minValue = Widget.Slider.MIN_VALUE,
                maxValue = rangeSliderPosition.endInclusive.roundToInt(),
                onChangeValue = { start ->
                    onRangeSliderValueChange(start..rangeSliderPosition.endInclusive)
                },
            )

            Spacer(modifier = Modifier.weight(1f))

            SliderIncDecButtons(
                value = rangeSliderPosition.endInclusive.roundToInt(),
                step = 1,
                minValue = rangeSliderPosition.start.roundToInt(),
                maxValue = Widget.Slider.MAX_VALUE,
                onChangeValue = { endInclusive ->
                    onRangeSliderValueChange(rangeSliderPosition.start..endInclusive)
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.slider_step_label))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = stepSliderPosition,
                onValueChange = onStepSliderValueChange,
                valueRange = Widget.Slider.MIN_STEP.toFloat()..sliderWidget.maxValue.toFloat(),
                steps = sliderWidget.maxValue - 1,
                modifier = Modifier.weight(1f)
            )

            SliderIncDecButtons(
                value = stepSliderPosition.roundToInt(),
                step = 1,
                minValue = Widget.Slider.MIN_STEP,
                maxValue = sliderWidget.maxValue,
                onChangeValue = onStepSliderValueChange,
            )
        }
    }
}