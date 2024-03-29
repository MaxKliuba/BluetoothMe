package com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.R
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.AsIcon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidget(
    widget: Widget.Slider,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    withTitlePadding: Boolean = false,
    isIncDecButtonsVisible: Boolean = true,
    overlay: @Composable BoxScope.() -> Unit,
) {
    val onSliderAction: (Int) -> Unit = { newValue ->
        if (widget.enabled) {
            onAction(widget.messageTag, widget.convertStateToMessageValue(newValue))
        }
    }
    BasicWidget(
        widgetTitle = widget.title,
        withTitlePadding = withTitlePadding,
        modifier = modifier,
        overlay = overlay,
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Slider(
                value = widget.state.toFloat(),
                onValueChange = { onSliderAction(it.roundToInt()) },
                valueRange = widget.minValue.toFloat()..widget.maxValue.toFloat(),
                steps = (widget.maxValue - widget.minValue - 1) / widget.step,
                interactionSource = interactionSource,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        colors = SliderDefaults.colors(),
                        thumbSize = DpSize(36.dp, 36.dp),
                    )
                    widget.icon.AsIcon(tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isIncDecButtonsVisible && widget.enabled) {
                    IconButton(onClick = { onSliderAction(widget.decValue()) }) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.dec_value_button)
                        )
                    }
                }

                Text(
                    text = widget.convertStateToMessageValue(widget.state),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (isIncDecButtonsVisible && widget.enabled) {
                    IconButton(onClick = { onSliderAction(widget.incValue()) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.inc_value_button)
                        )
                    }
                }
            }
        }
    }
}