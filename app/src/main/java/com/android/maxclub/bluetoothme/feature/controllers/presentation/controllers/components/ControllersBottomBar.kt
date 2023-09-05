package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Composable
fun ControllersBottomBar(
    onDeleteController: () -> Unit,
    onEditController: () -> Unit,
    onShareController: () -> Unit,
    onUnselectController: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = {
                    onDeleteController()
                    onUnselectController()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.delete_controller_button)
                )
            }

            IconButton(onClick = onEditController) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_controller_button)
                )
            }

            IconButton(onClick = onShareController) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_controller_button)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUnselectController) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done_button),
                )
            }
        },
        modifier = modifier
    )
}