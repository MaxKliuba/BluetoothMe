package com.android.maxclub.bluetoothme.feature.controllers.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM controllers WHERE controllers.isDeleted = 0 AND controllers.id = :controllerId")
    fun getControllerWithWidgetsById(controllerId: String): Flow<ControllerWithWidgetsResult>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM controllers")
    fun getNextControllerPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControllers(vararg controllers: ControllerEntity)

    @Query("UPDATE controllers SET position = :newPosition WHERE id = :controllerId")
    suspend fun updateControllerPositionById(controllerId: String, newPosition: Int)

    @Query("UPDATE controllers SET isDeleted = 1 WHERE id = :controllerId")
    suspend fun deleteControllerById(controllerId: String)

    @Query("UPDATE controllers SET isDeleted = 0 WHERE id = :controllerId")
    suspend fun tryRestoreControllerById(controllerId: String)

    @Query("DELETE FROM controllers WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedControllers()

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM widgets WHERE controllerId = :controllerId")
    fun getNextWidgetPosition(controllerId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(vararg widgets: WidgetEntity)

    @Query("UPDATE widgets SET isDeleted = 1 WHERE id = :widgetId")
    suspend fun deleteWidgetById(widgetId: String)

    @Query("UPDATE widgets SET isDeleted = 0 WHERE id = :widgetId")
    suspend fun tryRestoreWidgetById(widgetId: String)

    @Query("DELETE FROM widgets WHERE isDeleted = 1")
    suspend fun deleteMarkedAsDeletedWidgets()
}