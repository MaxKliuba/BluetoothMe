package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components

import android.view.KeyEvent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.android.maxclub.bluetoothme.R

@Suppress("OPT_IN_IS_NOT_ENABLED")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MessageTextField(
    value: String,
    isHintVisible: Boolean,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = if (isHintVisible) {
            { Text(text = stringResource(R.string.message_hint)) }
        } else null,
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onSend(value) }) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = stringResource(R.string.send_button),
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
            onSend = { onSend(value) }
        ),
        singleLine = true,
        maxLines = 1,
        modifier = modifier
            .onFocusChanged(onFocusChanged = onFocusChanged)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    onSend(value)
                }
                false
            }
    )
}