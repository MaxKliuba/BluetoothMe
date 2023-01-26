package com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages

data class Message(
    val type: Type,
    val value: String,
) {
    fun toByteArray(): ByteArray = value.toByteArray()

    override fun toString(): String = value

    sealed class Type {
        object Input : Type()
        object Output : Type()
        object Error : Type()
    }
}