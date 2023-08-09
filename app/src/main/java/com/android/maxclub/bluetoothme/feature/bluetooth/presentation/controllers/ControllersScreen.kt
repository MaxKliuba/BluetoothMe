package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.controllers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Composable
fun ControllersScreen(
    onClickNavigationIcon: () -> Unit,
) {
    Text(text = stringResource(id = R.string.controllers_screen_title))
}