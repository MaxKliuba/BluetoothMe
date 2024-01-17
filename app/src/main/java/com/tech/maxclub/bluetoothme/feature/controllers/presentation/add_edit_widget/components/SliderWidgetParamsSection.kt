package com.tech.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidgetParamsSection(
    sliderWidget: Widget.Slider,
    rangeSliderPosition: IntRange,
    onRangeValueChange: (Widget.Slider, Int, Int) -> Unit,
    stepSliderPosition: Int,
    onStepValueChange: (Widget.Slider, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.slider_range_label))
        Row {
            RangeSlider(
                value = rangeSliderPosition.first.toFloat()..rangeSliderPosition.last.toFloat(),
                onValueChange = {
                    onRangeValueChange(
                        sliderWidget,
                        it.start.roundToInt(),
                        it.endInclusive.roundToInt()
                    )
                },
                valueRange = Widget.Slider.MIN_VALUE.toFloat()..Widget.Slider.MAX_VALUE.toFloat(),
                steps = Widget.Slider.MAX_VALUE - Widget.Slider.MIN_VALUE - 1,
            )
        }
        Row {
            SliderIncDecButtons(
                value = rangeSliderPosition.first,
                step = 1,
                minValue = Widget.Slider.MIN_VALUE,
                maxValue = rangeSliderPosition.last,
                onChangeValue = { start ->
                    onRangeValueChange(sliderWidget, start, rangeSliderPosition.last)
                },
            )

            Spacer(modifier = Modifier.weight(1f))

            SliderIncDecButtons(
                value = rangeSliderPosition.last,
                step = 1,
                minValue = rangeSliderPosition.first,
                maxValue = Widget.Slider.MAX_VALUE,
                onChangeValue = { endInclusive ->
                    onRangeValueChange(sliderWidget, rangeSliderPosition.first, endInclusive)
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.slider_step_label))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = stepSliderPosition.toFloat(),
                onValueChange = {
                    onStepValueChange(sliderWidget, it.roundToInt())
                },
                valueRange = Widget.Slider.MIN_STEP.toFloat()..sliderWidget.maxValue.toFloat(),
                steps = sliderWidget.maxValue - 1,
                modifier = Modifier.weight(1f)
            )

            SliderIncDecButtons(
                value = stepSliderPosition,
                step = 1,
                minValue = Widget.Slider.MIN_STEP,
                maxValue = sliderWidget.maxValue,
                onChangeValue = {
                    onStepValueChange(sliderWidget, it)
                },
            )
        }
    }
}