package com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.widgets

import android.view.KeyEvent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.presentation.util.components.AsIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextWidget(
    widget: Widget.Text,
    enabled: Boolean,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    withTitlePadding: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit,
) {
    var value by rememberSaveable(widget.state) {
        mutableStateOf(widget.state)
    }

    BasicWidget(
        widgetTitle = widget.title,
        withTitlePadding = withTitlePadding,
        modifier = modifier,
        overlay = overlay,
    ) {
        TextField(
            value = value,
            onValueChange = { if (enabled) value = it },
            readOnly = !enabled,
            leadingIcon = if (widget.icon.isValid) {
                { widget.icon.AsIcon() }
            } else null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onAction(widget.messageTag, value) }),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        onAction(widget.messageTag, value)
                    }
                    false
                }
        )
    }
}