package com.tech.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tech.maxclub.bluetoothme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllersTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onOpenNavigationDrawer: () -> Unit,
    isControllerSelected: Boolean,
    onDeleteController: () -> Unit,
    onEditController: () -> Unit,
    onShareController: () -> Unit,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.controllers_screen_title))
        },
        navigationIcon = {
            IconButton(onClick = onOpenNavigationDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_button)
                )
            }
        },
        actions = {
            if (isControllerSelected) {
                IconButton(onClick = onShareController) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_controller_button)
                    )
                }

                IconButton(onClick = onEditController) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_controller_button)
                    )
                }

                IconButton(onClick = onDeleteController) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.delete_controller_button)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}