package com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.presentation.util.components.AsIcon

@Composable
fun ButtonWidget(
    widget: Widget.Button,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    withTitlePadding: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit,
) {
    BasicWidget(
        widgetTitle = widget.title,
        withTitlePadding = withTitlePadding,
        modifier = modifier,
        overlay = overlay,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .background(
                    if (widget.state) {
                        MaterialTheme.colorScheme.inversePrimary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    CircleShape
                )
                .pointerInput(Unit) {
                    if (widget.enabled) {
                        awaitEachGesture {
                            awaitFirstDown()

                            // OnPress actions here
                            onAction(widget.messageTag, widget.convertStateToMessageValue(true))

                            do {
                                val event = awaitPointerEvent()
                            } while (event.changes.any { it.pressed })

                            // OnRelease actions here
                            onAction(
                                widget.messageTag,
                                widget.convertStateToMessageValue(false)
                            )
                        }
                    }
                }
        ) {
            widget.icon.AsIcon(tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}