package com.tech.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components.messages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech.maxclub.bluetoothme.core.util.format
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputMessageItem(
    message: Message,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundShape = RoundedCornerShape(16.dp)

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = backgroundShape,
                )
                .clip(backgroundShape)
                .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
                .align(Alignment.CenterStart)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { onSelect(message.toMessageString()) },
                )
        ) {
            Text(
                text = message.toMessageString(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = message.timestamp.format(LocalContext.current),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}