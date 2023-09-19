package com.android.maxclub.bluetoothme.feature.controllers.domain.repositories

import com.android.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerWithWidgetsJson
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import kotlinx.coroutines.flow.Flow

interface ControllerRepository {

    fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCount>>

    fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgets>

    suspend fun addController(controller: Controller): Int

    suspend fun updateControllers(vararg controller: Controller)

    suspend fun deleteControllerById(controllerId: Int)

    suspend fun tryRestoreControllerById(controllerId: Int)

    suspend fun deleteMarkedAsDeletedControllers()

    fun getWidgetById(widgetId: Int): Flow<Widget<*>>

    suspend fun addWidget(widget: Widget<*>): Int

    suspend fun updateWidgets(vararg widget: Widget<*>)

    suspend fun deleteWidgetById(widgetId: Int)

    suspend fun tryRestoreWidgetById(widgetId: Int)

    suspend fun deleteMarkedAsDeletedWidgets()

    fun getControllerWithWidgetsJsonById(controllerId: Int): Flow<ControllerWithWidgetsJson>

    suspend fun addControllerWithWidgets(controllerWithWidgetsJson: ControllerWithWidgetsJson)
}