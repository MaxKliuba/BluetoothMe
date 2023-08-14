package com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages

data class Message(
    val type: Type,
    val value: String,
    val timestamp: Long,
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