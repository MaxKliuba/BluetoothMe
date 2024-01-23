package com.tech.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.usecases.messages.GetMessageTagsToValues
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetControllerWithWidgetsAndState @Inject constructor(
    private val controllerRepository: ControllerRepository,
    private val getMessageTagsToValues: GetMessageTagsToValues,
) {
    operator fun invoke(controllerId: Int): Flow<ControllerWithWidgets> =
        controllerRepository.getControllerWithWidgetsById(controllerId)
            .combine(getMessageTagsToValues()) { controllerWithWidgets, messageTagsToValues ->
                controllerWithWidgets.copy(
                    widgets = controllerWithWidgets.widgets
                        .sortedBy { it.position }
                        .map { it.copyWithState(messageTagsToValues[it.messageTag] ?: "") }
                )
            }
}