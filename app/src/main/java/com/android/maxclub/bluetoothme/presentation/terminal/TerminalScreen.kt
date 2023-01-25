package com.android.maxclub.bluetoothme.presentation.terminal

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Composable
fun TerminalScreen() {
    Text(text = stringResource(id = R.string.terminal_screen_title))
}