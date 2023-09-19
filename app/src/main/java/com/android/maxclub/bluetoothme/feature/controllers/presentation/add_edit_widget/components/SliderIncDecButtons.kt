package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_widget.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R

@Composable
fun SliderIncDecButtons(
    value: Int,
    step: Int,
    minValue: Int,
    maxValue: Int,
    onChangeValue: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val tryChangeValue: (Int) -> Unit = { newValue ->
        val isValid = newValue in minValue..maxValue
        if (isValid) onChangeValue(newValue.toFloat())
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = { tryChangeValue(value - step) }) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.dec_value_button)
            )
        }

        Text(
            text = value.toString(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.width(32.dp)
        )

        IconButton(onClick = { tryChangeValue(value + step) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.inc_value_button)
            )
        }
    }
}