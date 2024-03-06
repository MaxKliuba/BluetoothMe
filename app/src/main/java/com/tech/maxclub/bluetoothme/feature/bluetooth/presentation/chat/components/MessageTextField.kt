package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components

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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.tech.maxclub.bluetoothme.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MessageTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = stringResource(R.string.message_placeholder)) },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                IconButton(onClick = { onSend(value.text) }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(R.string.send_button),
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
            keyboardType = KeyboardType.Uri,
        ),
        keyboardActions = KeyboardActions(onSend = { onSend(value.text) }),
        singleLine = true,
        maxLines = 1,
        modifier = modifier
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    onSend(value.text)
                }
                false
            }
    )
}