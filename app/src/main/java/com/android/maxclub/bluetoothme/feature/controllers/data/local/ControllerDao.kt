package com.android.maxclub.bluetoothme.feature.controllers.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetCountResult
import com.android.maxclub.bluetoothme.feature.controllers.data.local.results.ControllerWithWidgetsResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ControllerDao {

    @Transaction
    @Query(
        "SELECT controllers.*, COUNT(widgets.id) AS widgetCount " +
                "FROM controllers LEFT JOIN (SELECT * FROM widgets WHERE isDeleted = 0) AS widgets " +
                "ON controllers.id = widgets.controllerId " +
                "WHERE controllers.isDeleted = 0 " +
                "GROUP BY controllers.id"
    )
    fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCountResult>>

    @Transaction
    @Query("SELECT * FROM controllers WHERE isDeleted = 0 AND id = :controllerId")
    fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgetsResult>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM controllers")
    suspend fun getNextControllerPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertController(controller: ControllerEntity): Long

    @Transaction
    suspend fun insertControllerWithNextPosition(controller: ControllerEntity): Long {
        val newController = if (controller.position == -1) {
            val nextPosition = getNextControllerPosition()
            controller.copy(position = nextPosition)
        } else {
            controller
        }

        return insertController(newController)
    }

    @Update
    suspend fun updateControllers(vararg controllers: ControllerEntity)

    @Query("UPDATE controllers SET isDeleted = 1 WHERE id = :controllerId")
    suspend fun deleteControllerById(controllerId: Int)

    @Query("UPDATE controllers SET isDeleted = 0 WHERE id = :controllerId")
    suspend fun tryRestoreControllerById(controllerId: Int)

    @Query("DELETE FROM controllers WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedControllers()

    @Query("SELECT * FROM widgets WHERE isDeleted = 0 AND id = :widgetId")
    fun getWidgetById(widgetId: Int): Flow<WidgetEntity>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM widgets WHERE controllerId = :controllerId")
    suspend fun getNextWidgetPosition(controllerId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<WidgetEntity>)

    @Transaction
    suspend fun insertWidgetWithNextPosition(widget: WidgetEntity): Long {
        val newWidget = getWidgetWithNextPosition(widget)

        return insertWidget(newWidget)
    }

    @Transaction
    suspend fun insertWidgetsWithNextPositions(widgets: List<WidgetEntity>) {
        val newWidgets = widgets.map { getWidgetWithNextPosition(it) }

        insertWidgets(newWidgets)
    }

    @Update
    suspend fun updateWidgets(vararg widgets: WidgetEntity)

    @Query("UPDATE widgets SET isDeleted = 1 WHERE id = :widgetId")
    suspend fun deleteWidgetById(widgetId: Int)

    @Query("UPDATE widgets SET isDeleted = 0 WHERE id = :widgetId")
    suspend fun tryRestoreWidgetById(widgetId: Int)

    @Query("DELETE FROM widgets WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedWidgets()

    @Transaction
    suspend fun insertControllerWithWidgets(
        controller: ControllerEntity,
        widgets: List<WidgetEntity>
    ) {
        val controllerId = insertControllerWithNextPosition(controller).toInt()
        val widgetsWithControllerId = widgets.map { it.copy(controllerId = controllerId) }

        insertWidgetsWithNextPositions(widgetsWithControllerId)
    }

    private suspend fun getWidgetWithNextPosition(widget: WidgetEntity): WidgetEntity =
        if (widget.position == -1) {
            val nextPosition = getNextWidgetPosition(widget.controllerId)
            widget.copy(position = nextPosition)
        } else {
            widget
        }
}