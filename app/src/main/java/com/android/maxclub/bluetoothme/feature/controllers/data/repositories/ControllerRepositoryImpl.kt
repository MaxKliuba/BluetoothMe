package com.android.maxclub.bluetoothme.feature.controllers.data.repositories

import com.android.maxclub.bluetoothme.feature.controllers.data.local.ControllerDao
import com.android.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.data.mappers.toControllerWithWidgets
import com.android.maxclub.bluetoothme.feature.controllers.data.mappers.toWidget
import com.android.maxclub.bluetoothme.feature.controllers.data.mappers.toWidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Controller
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgetCount
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerWithWidgets
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.Widget
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.repositories.ControllerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
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

    override fun getControllerWithWidgetsById(controllerId: UUID): Flow<ControllerWithWidgets> =
        controllerDao.getControllerWithWidgetsById(controllerId)
            .map { it.toControllerWithWidgets() }
            .flowOn(Dispatchers.IO)

    override suspend fun addController(controller: Controller) =
        withContext(Dispatchers.IO) {
            val controllerEntity = if (controller.position == -1) {
                val nextPosition = controllerDao.getNextControllerPosition()
                controller.copy(position = nextPosition)
            } else {
                controller
            }.toControllerEntity()

            controllerDao.insertControllers(controllerEntity)
        }

    override suspend fun updateControllerPositionById(controllerId: UUID, newPosition: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.updateControllerPositionById(controllerId, newPosition)
        }

    override suspend fun updateController(controller: Controller) =
        withContext(Dispatchers.IO) {
            controllerDao.updateControllers(controller.toControllerEntity())
        }

    override suspend fun deleteControllerById(controllerId: UUID) =
        withContext(Dispatchers.IO) {
            controllerDao.deleteControllerById(controllerId)
        }

    override suspend fun tryRestoreControllerById(controllerId: UUID) =
        withContext(Dispatchers.IO) {
            controllerDao.tryRestoreControllerById(controllerId)
        }

    override suspend fun deleteMarkedAsDeletedControllers() =
        withContext(Dispatchers.IO) {
            controllerDao.deleteMarkedAsDeletedControllers()
        }

    override fun getWidgetById(widgetId: UUID): Flow<Widget> =
        controllerDao.getWidgetById(widgetId)
            .map { it.toWidget() }
            .flowOn(Dispatchers.IO)

    override suspend fun addWidget(widget: Widget) =
        withContext(Dispatchers.IO) {
            val widgetEntity = if (widget.position == -1) {
                val nextPosition = controllerDao.getNextWidgetPosition(widget.controllerId)
                widget.toWidgetEntity().copy(position = nextPosition)
            } else {
                widget.toWidgetEntity()
            }

            controllerDao.insertWidgets(widgetEntity)
        }

    override suspend fun updateWidgetPositionById(widgetId: UUID, newPosition: Int) =
        withContext(Dispatchers.IO) {
            controllerDao.updateWidgetPositionById(widgetId, newPosition)
        }

    override suspend fun updateWidget(widget: Widget) =
        withContext(Dispatchers.IO) {
            controllerDao.updateWidgets(widget.toWidgetEntity())
        }

    override suspend fun deleteWidgetById(widgetId: UUID) =
        withContext(Dispatchers.IO) {
            controllerDao.deleteWidgetById(widgetId)
        }

    override suspend fun tryRestoreWidgetById(widgetId: UUID) =
        withContext(Dispatchers.IO) {
            controllerDao.tryRestoreWidgetById(widgetId)
        }

    override suspend fun deleteMarkedAsDeletedWidgets() =
        withContext(Dispatchers.IO) {
            controllerDao.deleteMarkedAsDeletedWidgets()
        }
}