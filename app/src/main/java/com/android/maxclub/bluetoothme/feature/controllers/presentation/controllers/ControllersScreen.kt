package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Composable
fun ControllersScreen(
    onOpenNavigationDrawer: () -> Unit,
) {
    Text(text = stringResource(id = R.string.controllers_screen_title))
}