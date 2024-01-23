package com.tech.maxclub.bluetoothme.feature.controllers.data.repositories

import com.tech.maxclub.bluetoothme.feature.controllers.data.local.ControllerDao
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerEntity
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toWidget
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toWidgetEntity
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ControllerRepositoryImpl @Inject constructor(
    private val controllerDao: ControllerDao,
) : ControllerRepository {

    override fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCount>> =
        controllerDao.getControllersWithWidgetCount()
            .map { list ->
                list.map { it.toControllerWithWidgetCount() }
            }

    override fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgets> =
        controllerDao.getControllerWithWidgetsById(controllerId)
            .map { it.toControllerWithWidgets() }

    override suspend fun addController(controller: Controller): Int {
        val controllerEntity = controller.toControllerEntity()

        return controllerDao.insertControllerWithNextPosition(controllerEntity).toInt()
    }

    override suspend fun updateControllers(vararg controller: Controller) {
        controllerDao.updateControllers(
            *controller.map { it.toControllerEntity() }.toTypedArray()
        )
    }

    override suspend fun deleteControllerById(controllerId: Int) {
        controllerDao.deleteControllerById(controllerId)
    }

    override suspend fun tryRestoreControllerById(controllerId: Int) {
        controllerDao.tryRestoreControllerById(controllerId)
    }

    override suspend fun deleteMarkedAsDeletedControllers() {
        controllerDao.deleteMarkedAsDeletedControllers()
    }

    override fun getWidgetById(widgetId: Int): Flow<Widget<*>> =
        controllerDao.getWidgetById(widgetId)
            .map { it.toWidget() }

    override suspend fun addWidget(widget: Widget<*>): Int {
        val widgetEntity = widget.toWidgetEntity()

        return controllerDao.insertWidgetWithNextPosition(widgetEntity).toInt()
    }

    override suspend fun updateWidgets(vararg widget: Widget<*>) {
        controllerDao.updateWidgets(
            *widget.map { it.toWidgetEntity() }.toTypedArray()
        )
    }

    override suspend fun deleteWidgetById(widgetId: Int) {
        controllerDao.deleteWidgetById(widgetId)
    }

    override suspend fun tryRestoreWidgetById(widgetId: Int) {
        controllerDao.tryRestoreWidgetById(widgetId)
    }

    override suspend fun deleteMarkedAsDeletedWidgets() {
        controllerDao.deleteMarkedAsDeletedWidgets()
    }

    override fun getControllerWithWidgetsJsonById(controllerId: Int): Flow<ControllerWithWidgetsJson> =
        controllerDao.getControllerWithWidgetsById(controllerId)
            .map { it.toControllerWithWidgetsJson() }

    override suspend fun addControllerWithWidgets(controllerWithWidgetsJson: ControllerWithWidgetsJson) {
        controllerDao.insertControllerWithWidgets(
            controller = controllerWithWidgetsJson.controller.toControllerEntity(),
            widgets = controllerWithWidgetsJson.widgets.map { it.toWidgetEntity(0) }
        )
    }
}