package com.android.maxclub.bluetoothme.feature.controllers.presentation.add_edit_controller.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.android.maxclub.bluetoothme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditControllerTopBar(
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    withAccelerometer: Boolean,
    onWithAccelerometerChange: (Boolean) -> Unit,
    withVoiceInput: Boolean,
    onWithVoiceInputChange: (Boolean) -> Unit,
    withRefresh: Boolean,
    onWithRefreshChange: (Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            TitleTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        },
        actions = {
            FilledIconToggleButton(
                checked = withAccelerometer,
                onCheckedChange = onWithAccelerometerChange,
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = stringResource(R.string.with_accelerometer_button),
                )
            }

            FilledIconToggleButton(
                checked = withVoiceInput,
                onCheckedChange = onWithVoiceInputChange,
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = stringResource(R.string.with_voice_input_button),
                )
            }

            FilledIconToggleButton(
                checked = withRefresh,
                onCheckedChange = onWithRefreshChange,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_get_refresh_state_24),
                    contentDescription = stringResource(R.string.with_refresh_button),
                )
            }
        },
        modifier = modifier
    )
}