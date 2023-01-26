package com.android.maxclub.bluetoothme.feature_bluetooth.domain.usecases.messages

import com.android.maxclub.bluetoothme.feature_bluetooth.domain.repositories.BluetoothRepository
import javax.inject.Inject

class DeleteMessages @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() = repository.deleteMessages()
}