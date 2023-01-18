package com.android.maxclub.bluetoothme.domain.exceptions

import com.android.maxclub.bluetoothme.domain.messages.Message

data class WriteMessageException(val mes: Message) : Exception()
