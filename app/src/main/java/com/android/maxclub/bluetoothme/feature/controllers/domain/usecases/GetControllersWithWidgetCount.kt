package com.android.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetControllersWithWidgetCount @Inject constructor(
    private val repository: ControllerRepository
) {
    operator fun invoke() =
        repository.getControllersWithWidgetCount()
            .map { list ->
                list.sortedBy { it.controller.position }
            }
}