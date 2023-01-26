package com.android.maxclub.bluetoothme.feature_bluetooth.presentation.terminal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    onClickNavigationIcon: () -> Unit,
    viewModel: TerminalViewModel = hiltViewModel(),
) {
    val messages by viewModel.uiState

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.terminal_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onClickNavigationIcon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_24),
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                    }
                },
                actions = {
                    if (messages.isNotEmpty()) {
                        IconButton(onClick = viewModel::deleteMessages) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete_messages_24),
                                contentDescription = stringResource(id = R.string.delete_messages_button)
                            )
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            items(
                items = messages,
            ) { message ->
                Text(text = message.value)
            }
        }
    }
}