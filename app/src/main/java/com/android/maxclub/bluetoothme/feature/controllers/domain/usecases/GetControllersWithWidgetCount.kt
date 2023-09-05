package com.android.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetControllersWithWidgetCount @Inject constructor(
    private val repository: ControllerRepository
) {
    val comparator = Comparator<ControllerWithWidgetCount> { item0, item1 ->
        item0.controller.position.compareTo(item1.controller.position)
    }

    operator fun invoke() =
        repository.getControllersWithWidgetCount()
            .map { list ->
                list.sortedWith(comparator)
            }
}