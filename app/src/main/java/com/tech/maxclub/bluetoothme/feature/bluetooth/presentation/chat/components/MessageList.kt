package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.messages.InputMessageItem
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.messages.LogMessageItem
import com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.messages.OutputMessageItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageList(
    messages: List<Message>,
    onSelectMessage: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = scrollState,
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier,
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            when (message.type) {
                Message.Type.Input -> InputMessageItem(
                    message = message,
                    onSelect = onSelectMessage,
                    modifier = Modifier.animateItemPlacement()
                )

                Message.Type.Output -> OutputMessageItem(
                    message = message,
                    hasError = false,
                    onSelect = onSelectMessage,
                    modifier = Modifier.animateItemPlacement()
                )

                Message.Type.Error -> OutputMessageItem(
                    message = message,
                    hasError = true,
                    onSelect = onSelectMessage,
                    modifier = Modifier.animateItemPlacement()
                )

                Message.Type.Log -> LogMessageItem(
                    message = message,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}