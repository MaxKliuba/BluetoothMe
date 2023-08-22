package com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages

data class Message(
    val type: Type,
    val tag: String,
    val value: String,
    val timestamp: Long,
) {
    fun toMessageString(): String = "${if (tag.isNotEmpty()) "$tag$TAG_TERMINATOR" else ""}$value"

    fun toByteArray(): ByteArray = toString().toByteArray()

    override fun toString(): String = "${toMessageString()}$MESSAGE_TERMINATOR"

    sealed class Type {
        object Input : Type()
        object Output : Type()
        object Error : Type()
        object Log : Type()
    }

    companion object {
        const val TAG_TERMINATOR = '/'
        const val MESSAGE_TERMINATOR = '\n'
    }
}