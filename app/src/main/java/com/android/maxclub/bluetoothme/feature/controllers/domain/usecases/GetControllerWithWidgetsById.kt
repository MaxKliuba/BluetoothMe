package com.android.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GetControllerWithWidgetsById @Inject constructor(
    private val repository: ControllerRepository
) {
    operator fun invoke(controllerId: UUID) =
        repository.getControllerWithWidgetsById(controllerId)
            .map { controllerWithWidgets ->
                controllerWithWidgets.copy(
                    widgets = controllerWithWidgets.widgets.sortedBy { it.position }
                )
            }
}