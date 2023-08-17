package com.android.maxclub.bluetoothme.feature.bluetooth.presentation.chat.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.maxclub.bluetoothme.R
import com.android.maxclub.bluetoothme.core.util.format
import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OutputMessageItem(
    message: Message,
    hasError: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundShape = RoundedCornerShape(16.dp)

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Box(modifier = modifier.fillMaxWidth(0.9f)) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = backgroundShape,
                    )
                    .clip(backgroundShape)
                    .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
                    .align(Alignment.CenterEnd)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { onSelect(message.toString()) },
                    )
            ) {
                Text(
                    text = message.toString().trim('\n'),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    if (hasError) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = stringResource(R.string.error_label),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = message.timestamp.format(LocalContext.current),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}