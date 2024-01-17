package com.tech.maxclub.bluetoothme.feature.controllers.domain.usecases

import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetControllersWithWidgetCount @Inject constructor(
    private val repository: ControllerRepository
) {
    val comparator = Comparator<ControllerWithWidgetCount> { item0, item1 ->
        item0.controller.position.compareTo(item1.controller.position)
    }

    operator fun invoke(): Flow<List<ControllerWithWidgetCount>> =
        repository.getControllersWithWidgetCount()
            .map { list ->
                list.sortedWith(comparator)
            }
}