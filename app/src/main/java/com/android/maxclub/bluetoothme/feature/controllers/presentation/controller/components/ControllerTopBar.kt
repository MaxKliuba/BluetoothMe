package com.android.maxclub.bluetoothme.feature.controllers.presentation.controller.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerTopBar(
    controllerTitle: String,
    bluetoothState: String,
    onClickWithAccelerometer: (() -> Unit)?,
    onClickWithVoiceInput: (() -> Unit)?,
    onClickWithRefresh: (() -> Unit)?,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(text = controllerTitle)
                Text(
                    text = bluetoothState,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_button)
                )
            }
        },
        actions = {
            onClickWithAccelerometer?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.with_accelerometer_button),
                    )
                }
            }

            onClickWithVoiceInput?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = stringResource(R.string.with_voice_input_button),
                    )
                }
            }

            onClickWithRefresh?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_get_refresh_state_24),
                        contentDescription = stringResource(R.string.with_refresh_button),
                    )
                }
            }
        },
        modifier = modifier
    )
}