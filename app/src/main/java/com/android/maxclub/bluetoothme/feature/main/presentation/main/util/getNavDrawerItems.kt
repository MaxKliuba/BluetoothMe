package com.android.maxclub.bluetoothme.feature.main.presentation.main.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.core.util.Screen

@Composable
fun getNavDrawerItems(
    connectionBadge: NavDrawerItem.Badge.Button?,
    messageCountBadge: NavDrawerItem.Badge.Button?
): List<NavDrawerItem> {
    val connectionItem by remember(
        connectionBadge?.isProgressIndicatorVisible,
        connectionBadge?.getValue,
        connectionBadge?.isEnabled,
    ) {
        mutableStateOf(
            NavDrawerItem(
                type = NavDrawerItem.Type.Route(Screen.Connection.route),
                icon = R.drawable.ic_bluetooth_connection_24,
                label = R.string.connection_screen_title,
                badge = connectionBadge,
            )
        )
    }
    val controllersItem by remember {
        mutableStateOf(
            NavDrawerItem(
                type = NavDrawerItem.Type.Route(Screen.Controllers.route),
                icon = R.drawable.ic_controllers_24,
                label = R.string.controllers_screen_title,
                badge = null,
            )
        )
    }
    val chatItem by remember(messageCountBadge?.getValue) {
        mutableStateOf(
            NavDrawerItem(
                type = NavDrawerItem.Type.Route(Screen.Chat.route),
                icon = R.drawable.ic_chat_24,
                label = R.string.chat_screen_title,
                badge = messageCountBadge,
            )
        )
    }
    val helpItem by remember {
        mutableStateOf(
            NavDrawerItem(
                type = NavDrawerItem.Type.Url(Screen.Help.route),
                icon = R.drawable.ic_help_24,
                label = R.string.help_screen_title,
                badge = null,
            )
        )
    }

    return listOf(connectionItem, controllersItem, chatItem, helpItem)
}