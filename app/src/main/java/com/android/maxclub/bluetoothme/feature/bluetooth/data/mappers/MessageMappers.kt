package com.android.maxclub.bluetoothme.feature.bluetooth.data.mappers

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import java.util.Date

fun ByteArray.toMessage(type: Message.Type): Message =
    String(this).toMessage(type)

fun String.toMessage(type: Message.Type): Message {
    val value = this.substringBefore(Message.MESSAGE_TERMINATOR)

    return Message(
        type = type,
        value = value,
        timestamp = Date().time,
    )
}