package com.tech.maxclub.bluetoothme.feature.controllers.data.repositories

import com.tech.maxclub.bluetoothme.feature.controllers.data.local.ControllerDao
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerEntity
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toWidget
import com.tech.maxclub.bluetoothme.feature.controllers.data.mappers.toWidgetEntity
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.share.ControllerWithWidgetsJson
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.tech.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ControllerRepositoryImpl @Inject constructor(
    private val controllerDao: ControllerDao,
) : ControllerRepository {

    override fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCount>> =
        controllerDao.getControllersWithWidgetCount()
            .map { list ->
                list.map { it.toControllerWithWidgetCount() }
            }
            .flowOn(Dispatchers.IO)

    override fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgets> =
        controllerDao.getControllerWithWidgetsById(controllerId)
            .map { it.toControllerWithWidgets() }
            .flowOn(Dispatchers.IO)

    override suspend fun addController(controller: Controller): Int =
        withContext(Dispatchers.IO) {
            val controllerEntity = controller.toControllerEntity()

            controllerDao.insertControllerWithNextPosition(controllerEntity).toInt()
        }

    override suspend fun updateControllers(vararg controller: Controller) =
        withContext(Dispatchers.IO) {
            controllerDao.updateControllers(
                *controller.map { it.toControllerEntity() }.toTypedArray()
            )
        }

    override suspend fun deleteControllerById(controllerId: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.deleteControllerById(controllerId)
        }

    override suspend fun tryRestoreControllerById(controllerId: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.tryRestoreControllerById(controllerId)
        }

    override suspend fun deleteMarkedAsDeletedControllers() =
        withContext(Dispatchers.IO) {
            controllerDao.deleteMarkedAsDeletedControllers()
        }

    override fun getWidgetById(widgetId: Int): Flow<Widget<*>> =
        controllerDao.getWidgetById(widgetId)
            .map { it.toWidget() }
            .flowOn(Dispatchers.IO)

    override suspend fun addWidget(widget: Widget<*>): Int =
        withContext(Dispatchers.IO) {
            val widgetEntity = widget.toWidgetEntity()

            controllerDao.insertWidgetWithNextPosition(widgetEntity).toInt()
        }

    override suspend fun updateWidgets(vararg widget: Widget<*>) =
        withContext(Dispatchers.IO) {
            controllerDao.updateWidgets(
                *widget.map { it.toWidgetEntity() }.toTypedArray()
            )
        }

    override suspend fun deleteWidgetById(widgetId: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.deleteWidgetById(widgetId)
        }

    override suspend fun tryRestoreWidgetById(widgetId: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.tryRestoreWidgetById(widgetId)
        }

    override suspend fun deleteMarkedAsDeletedWidgets() =
        withContext(Dispatchers.IO) {
            controllerDao.deleteMarkedAsDeletedWidgets()
        }

    override fun getControllerWithWidgetsJsonById(controllerId: Int): Flow<ControllerWithWidgetsJson> =
        controllerDao.getControllerWithWidgetsById(controllerId)
            .map { it.toControllerWithWidgetsJson() }
            .flowOn(Dispatchers.IO)

    override suspend fun addControllerWithWidgets(controllerWithWidgetsJson: ControllerWithWidgetsJson) =
        withContext(Dispatchers.IO) {
            controllerDao.insertControllerWithWidgets(
                controller = controllerWithWidgetsJson.controller.toControllerEntity(),
                widgets = controllerWithWidgetsJson.widgets.map { it.toWidgetEntity(0) }
            )
        }
}