package com.tech.maxclub.bluetoothme.feature.controllers.presentation.share_controller.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.tech.maxclub.bluetoothme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareControllerTopBar(
    controllerTitle: String,
    onSaveFile: () -> Unit,
    onShare: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = controllerTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveFile) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = stringResource(R.string.save_as_file_button),
                )
            }
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(id = R.string.share_controller_button),
                )
            }
        },
        modifier = modifier
    )
}