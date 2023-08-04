package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.util.NavDrawerBadge
import com.android.maxclub.bluetoothme.feature_bluetooth.presentation.main.util.NavDrawerItem

@Composable
fun NavigationDrawer(
    selectedItem: String,
    items: List<NavDrawerItem>,
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
                    style = MaterialTheme.typography.headlineMedium,
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
                                is NavDrawerBadge.Text -> {
                                    Text(text = item.badge.getValue())
                                }

                                is NavDrawerBadge.Button -> {
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

                                null -> Unit
                            }
                        },
                        selected = item.route == selectedItem,
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