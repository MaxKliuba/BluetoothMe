package com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import java.util.Date

fun ByteArray.toMessage(type: Message.Type): Message =
    String(this).toMessage(type)

fun String.toMessage(type: Message.Type): Message {
    val tag = this.substringBefore(Message.TAG_TERMINATOR, missingDelimiterValue = "")
    val value = this.substringAfter(Message.TAG_TERMINATOR)
        .substringBefore(Message.MESSAGE_TERMINATOR)

    return Message(
        type = type,
        tag = tag,
        value = value,
        timestamp = Date().time,
    )
}