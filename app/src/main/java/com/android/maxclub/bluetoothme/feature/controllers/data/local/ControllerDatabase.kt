package com.android.maxclub.bluetoothme.feature.controllers.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.ControllerEntity
import com.android.maxclub.bluetoothme.feature.controllers.data.local.entities.WidgetEntity

@Database(
    entities = [ControllerEntity::class, WidgetEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ControllerDatabase : RoomDatabase() {
    abstract val controllerDao: ControllerDao

    companion object {
        private const val DATABASE_NAME = "controller_db"

        @Volatile
        private var INSTANCE: ControllerDatabase? = null

        fun getInstance(context: Context): ControllerDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ControllerDatabase::class.java,
                    DATABASE_NAME,
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
    }
}