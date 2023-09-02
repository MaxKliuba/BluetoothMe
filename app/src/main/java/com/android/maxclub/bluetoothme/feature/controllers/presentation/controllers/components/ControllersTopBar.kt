package com.android.maxclub.bluetoothme.feature.controllers.presentation.controllers.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
fun ControllersTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onOpenNavigationDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.controllers_screen_title))
        },
        navigationIcon = {
            IconButton(onClick = onOpenNavigationDrawer) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_24),
                    contentDescription = stringResource(R.string.menu_button)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}