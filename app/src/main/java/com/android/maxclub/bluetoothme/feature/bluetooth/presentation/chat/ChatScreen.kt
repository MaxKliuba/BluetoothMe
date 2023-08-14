package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat

import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers.toString
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.bluetooth.models.BluetoothState
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.InputMessageItem
import com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.OutputMessageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onClickNavigationIcon: () -> Unit,
    onShowSendErrorMessage: () -> Unit,
    bluetoothState: BluetoothState,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val state by viewModel.uiState
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val onMessageValueChange: (String) -> Unit = remember {
        { viewModel.onEvent(ChatUiEvent.OnChangeMessageValue(it)) }
    }
    val onFocusChanged: (FocusState) -> Unit = remember {
        { viewModel.onEvent(ChatUiEvent.OnFocusChanged(it)) }
    }
    val onSendMessage: (String) -> Unit = remember {
        { viewModel.onEvent(ChatUiEvent.OnSendMessage(it)) }
    }
    val onSelectMessage: (String) -> Unit = remember {
        { viewModel.onEvent(ChatUiEvent.OnChangeMessageValue(it)) }
    }
    val onDeleteMessages: () -> Unit = remember {
        { viewModel.onEvent(ChatUiEvent.OnDeleteMessages) }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is ChatUiAction.ScrollToBottom -> {
                    launch(Dispatchers.Main) {
                        scrollState.scrollToItem(0)
                    }
                }

                is ChatUiAction.ShowSendErrorMessage -> {
                    onShowSendErrorMessage()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.chat_screen_title))
                        Text(
                            text = bluetoothState.toString(context),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClickNavigationIcon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_24),
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                    }
                },
                actions = {
                    if (state.messages.isNotEmpty()) {
                        IconButton(onClick = onDeleteMessages) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete_messages_24),
                                contentDescription = stringResource(id = R.string.delete_messages_button)
                            )
                        }
                    }
                },
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
            LazyColumn(
                state = scrollState,
                reverseLayout = true,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                items(
                    items = state.messages,
                ) { message ->
                    when (message.type) {
                        Message.Type.Input -> InputMessageItem(
                            message = message,
                            onSelect = onSelectMessage,
                        )

                        Message.Type.Output -> OutputMessageItem(
                            message = message,
                            hasError = false,
                            onSelect = onSelectMessage,
                        )

                        Message.Type.Error -> OutputMessageItem(
                            message = message,
                            hasError = true,
                            onSelect = onSelectMessage,
                        )
                    }
                }
            }

            TextField(
                value = state.messageValue,
                onValueChange = onMessageValueChange,
                label = if (state.isHintVisible) {
                    { Text(text = stringResource(R.string.message_hint)) }
                } else null,
                trailingIcon = if (state.messageValue.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSendMessage(state.messageValue) }) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = stringResource(R.string.send_button_text),
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { onSendMessage(state.messageValue) }
                ),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 4.dp)
                    .onFocusChanged(onFocusChanged = onFocusChanged)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                            onSendMessage(state.messageValue)
                        }
                        false
                    }
            )
        }
    }
}