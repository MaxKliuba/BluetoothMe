package com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages

data class Message(
    val type: Type,
    val value: String,
) {
    fun toByteArray(): ByteArray = toString().toByteArray()

    override fun toString(): String = "$value$MESSAGE_TERMINATOR"

    sealed class Type {
        object Input : Type()
        object Output : Type()
        object Error : Type()
    }

    companion object {
        const val MESSAGE_TERMINATOR = '\n'
    }
}

fun ByteArray.toMessage(type: Message.Type): Message =
    String(this).toMessage(type)

fun String.toMessage(type: Message.Type): Message {
    val value = this.substringBefore(Message.MESSAGE_TERMINATOR)

    return Message(
        type = type,
        value = value,
    )
}