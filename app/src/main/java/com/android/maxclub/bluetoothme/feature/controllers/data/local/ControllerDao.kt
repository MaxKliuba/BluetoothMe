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

    @Query(
        "SELECT controllers.*, COUNT(widgets.id) AS widgetCount " +
                "FROM controllers LEFT JOIN widgets ON controllers.id = widgets.controllerId " +
                "GROUP BY controllers.id"
    )
    fun getControllersWithWidgetCount(): Flow<List<ControllerWithWidgetCountResult>>

    @Transaction
    @Query("SELECT * FROM controllers WHERE id = :controllerId")
    fun getControllerWithWidgetsById(controllerId: Int): Flow<ControllerWithWidgetsResult>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM controllers")
    fun getNextControllerPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControllers(vararg controllers: ControllerEntity)

    @Update
    suspend fun updateControllers(vararg controllers: ControllerEntity)

    @Query("UPDATE controllers SET position = :newPosition WHERE id = :controllerId")
    suspend fun updateControllerPositionById(controllerId: Int, newPosition: Int)

    @Query("UPDATE controllers SET isDeleted = 1 WHERE id = :controllerId")
    suspend fun deleteControllerById(controllerId: Int)

    @Query("UPDATE controllers SET isDeleted = 0 WHERE id = :controllerId")
    suspend fun tryRestoreControllerById(controllerId: Int)

    @Query("DELETE FROM controllers WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedControllers()

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM widgets WHERE controllerId = :controllerId")
    fun getNextWidgetPosition(controllerId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(vararg widgets: WidgetEntity)

    @Update
    suspend fun updateWidgets(vararg widgets: WidgetEntity)

    @Query("UPDATE widgets SET isDeleted = 1 WHERE id = :widgetId")
    suspend fun deleteWidgetById(widgetId: Int)

    @Query("UPDATE widgets SET isDeleted = 0 WHERE id = :widgetId")
    suspend fun tryRestoreWidgetById(widgetId: Int)

    @Query("DELETE FROM widgets WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedWidgets()
}