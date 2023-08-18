package com.android.maxclub.bluetoothme.feature.controllers.domain.repositories

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import kotlinx.coroutines.flow.Flow

interface ControllerRepository {

    fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCount>>

    fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgets>

    suspend fun addController(controller: Controller)

    suspend fun updateController(controller: Controller)

    suspend fun updateControllerPositionById(controllerId: Int, newPosition: Int)

    suspend fun deleteControllerById(controllerId: Int)

    suspend fun tryRestoreControllerById(controllerId: Int)

    suspend fun deleteMarkedAsDeletedControllers()

    suspend fun addWidget(widget: Widget)

    suspend fun updateWidget(widget: Widget)

    suspend fun deleteWidgetById(widgetId: Int)

    suspend fun tryRestoreWidgetById(widgetId: Int)

    suspend fun deleteMarkedAsDeletedWidgets()
}