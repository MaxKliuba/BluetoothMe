package com.android.maxclub.bluetoothme.core.exceptions

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

data class WriteMessageException(val mes: Message) : Exception()
