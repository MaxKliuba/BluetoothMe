package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.ChatTopBar
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.MessageList
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.MessageTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    bluetoothState: BluetoothState,
    onShowSendingErrorMessage: () -> Unit,
    onOpenNavigationDrawer: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState
    val isDeleteMessageButtonVisible by remember { derivedStateOf { state.messages.isNotEmpty() } }

    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ChatUiAction.ScrollToBottom -> {
                    launch {
                        delay(10)
                        scrollState.animateScrollToItem(0)
                    }
                }

                is ChatUiAction.ShowSendingErrorMessage -> {
                    onShowSendingErrorMessage()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                bluetoothState = bluetoothState.toString(context),
                isDeleteMessageButtonVisible = isDeleteMessageButtonVisible,
                onDeleteMessages = viewModel::deleteMessages,
                onOpenNavigationDrawer = onOpenNavigationDrawer,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MessageList(
                messages = state.messages,
                onSelectMessage = viewModel::tryChangeMessageValue,
                scrollState = scrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            MessageTextField(
                value = state.messageValue,
                onValueChange = viewModel::tryChangeMessageValue,
                onSend = viewModel::trySendMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}