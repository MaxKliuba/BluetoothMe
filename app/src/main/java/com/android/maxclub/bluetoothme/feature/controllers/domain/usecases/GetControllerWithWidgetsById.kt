package com.android.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetControllerWithWidgetsById @Inject constructor(
    private val repository: ControllerRepository
) {
    val comparator = Comparator<Widget> { item0, item1 ->
        item0.position.compareTo(item1.position)
    }

    operator fun invoke(controllerId: Int) =
        repository.getControllerWithWidgetsById(controllerId)
            .map { controllerWithWidgets ->
                controllerWithWidgets.copy(
                    widgets = controllerWithWidgets.widgets.sortedWith(comparator)
                )
            }
}