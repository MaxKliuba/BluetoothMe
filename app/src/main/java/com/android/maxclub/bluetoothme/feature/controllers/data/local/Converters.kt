package com.android.maxclub.bluetoothme.feature.controllers.data.local

import androidx.room.TypeConverter
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.android.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

class Converters {
    @TypeConverter
    fun fromControllerColumns(columns: ControllerColumns?): Int? = columns?.count

    @TypeConverter
    fun toControllerColumns(count: Int?): ControllerColumns? =
        ControllerColumns.values().find { it.count == count }

    @TypeConverter
    fun fromWidgetType(type: WidgetType?): Int? = type?.ordinal

    @TypeConverter
    fun toWidgetType(ordinal: Int?): WidgetType? = ordinal?.let { WidgetType.values()[it] }

    @TypeConverter
    fun fromWidgetSize(size: WidgetSize?): Int? = size?.span

    @TypeConverter
    fun toWidgetSize(span: Int?): WidgetSize? = WidgetSize.values().find { it.span == span }
}