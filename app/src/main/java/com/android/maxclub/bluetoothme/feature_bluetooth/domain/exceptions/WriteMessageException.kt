package com.android.maxclub.bluetoothme.feature_bluetooth.domain.exceptions

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.messages.Message

data class WriteMessageException(val mes: Message) : Exception()
