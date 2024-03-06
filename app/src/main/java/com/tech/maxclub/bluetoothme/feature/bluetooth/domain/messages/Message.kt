package com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages

import java.util.UUID

data class Message(
    val id: UUID = UUID.randomUUID(),
    val type: Type,
    val tag: String,
    val value: String,
    val timestamp: Long,
) {
    fun toMessageString(): String = "${if (tag.isNotEmpty()) "$tag$TAG_TERMINATOR" else ""}$value"

    fun toByteArray(): ByteArray = toString().toByteArray()

    override fun toString(): String = "${toMessageString()}$MESSAGE_TERMINATOR"

    sealed class Type {
        data object Input : Type()
        data object Output : Type()
        data object Error : Type()
        data object Log : Type()
    }

    companion object {
        const val TAG_TERMINATOR = '/'
        const val MESSAGE_TERMINATOR = '\n'
    }
}