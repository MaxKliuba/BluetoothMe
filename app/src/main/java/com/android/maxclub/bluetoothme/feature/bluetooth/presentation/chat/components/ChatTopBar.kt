package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.maxclub.bluetoothme.R

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    bluetoothState: String,
    isDeleteMessageButtonVisible: Boolean,
    onDeleteMessages: () -> Unit,
    onOpenNavigationDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(text = stringResource(id = R.string.chat_screen_title))
                Text(
                    text = bluetoothState,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onOpenNavigationDrawer) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_24),
                    contentDescription = stringResource(R.string.menu_button)
                )
            }
        },
        actions = {
            if (isDeleteMessageButtonVisible) {
                IconButton(onClick = onDeleteMessages) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.delete_messages_button)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}