package com.android.maxclub.bluetoothme.presentation.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.presentation.util.Badge
import com.android.maxclub.bluetoothme.presentation.util.NavigationDrawerItem

@Composable
fun NavigationDrawer(
    currentRoute: String?,
    items: List<NavigationDrawerItem>,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(28.dp)
                )

                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                            )
                        },
                        label = { Text(text = stringResource(id = item.label)) },
                        badge = {
                            when (item.badge) {
                                is Badge.Text -> {
                                    Text(text = item.badge.getValue())
                                }
                                is Badge.Button -> {
                                    TextButton(
                                        onClick = item.badge.onClick,
                                        enabled = item.badge.isEnabled,
                                        modifier = Modifier.widthIn(min = 40.dp),
                                    ) {
                                        if (item.badge.withIndicator) {
                                            CircularProgressIndicator(
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(text = item.badge.getValue())
                                    }
                                }
                                null -> {}
                            }
                        },
                        selected = item.route == currentRoute,
                        onClick = { item.onClick(item.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
        content = content,
        modifier = modifier,
    )
}