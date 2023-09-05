package com.android.maxclub.bluetoothme.feature.controllers.domain.repositories

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ControllerRepository {

    fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCount>>

    fun getControllerWithWidgetsById(controllerId: UUID): Flow<ControllerWithWidgets>

    suspend fun addController(controller: Controller)

    suspend fun updateControllers(vararg controller: Controller)

    suspend fun deleteControllerById(controllerId: UUID)

    suspend fun tryRestoreControllerById(controllerId: UUID)

    suspend fun deleteMarkedAsDeletedControllers()

    fun getWidgetById(widgetId: UUID): Flow<Widget>

    suspend fun addWidget(widget: Widget)

    suspend fun updateWidgets(vararg widget: Widget)

    suspend fun deleteWidgetById(widgetId: UUID)

    suspend fun tryRestoreWidgetById(widgetId: UUID)

    suspend fun deleteMarkedAsDeletedWidgets()
}