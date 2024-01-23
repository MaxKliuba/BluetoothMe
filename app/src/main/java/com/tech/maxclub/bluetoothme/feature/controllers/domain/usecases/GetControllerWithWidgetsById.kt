package com.tech.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetControllerWithWidgetsById @Inject constructor(
    private val repository: ControllerRepository
) {
    operator fun invoke(controllerId: Int): Flow<ControllerWithWidgets> =
        repository.getControllerWithWidgetsById(controllerId)
            .map { controllerWithWidgets ->
                controllerWithWidgets.copy(
                    widgets = controllerWithWidgets.widgets.sortedBy { it.position }
                )
            }
}