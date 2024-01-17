package com.tech.maxclub.bluetoothme.core.exceptions

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message

data class WriteMessageException(val mes: Message) : Exception()
