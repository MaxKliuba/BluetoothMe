package com.android.maxclub.bluetoothme.feature.main.presentation.main.components

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
import com.android.maxclub.bluetoothme.feature.main.presentation.main.util.NavDrawerItem

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    currentDestination: String,
    items: List<NavDrawerItem>,
    onSelect: (NavDrawerItem.Type) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = items.any { it.type.value == currentDestination },
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
                                is NavDrawerItem.Badge.Text -> {
                                    Text(text = item.badge.getValue())
                                }

                                is NavDrawerItem.Badge.Button -> {
                                    TextButton(
                                        onClick = item.badge.onClick,
                                        enabled = item.badge.isEnabled,
                                        modifier = Modifier.widthIn(min = 40.dp),
                                    ) {
                                        if (item.badge.withProgressIndicator) {
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
                        selected = item.type.value == currentDestination,
                        onClick = { onSelect(item.type) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
        content = content,
        modifier = modifier,
    )
}