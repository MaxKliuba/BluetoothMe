package com.tech.maxclub.bluetoothme.feature.controllers.data.local

import androidx.room.TypeConverter
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.ControllerColumns
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetIcon
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetSize
import com.tech.maxclub.bluetoothme.feature.controllers.domain.models.WidgetType

class Converters {
    @TypeConverter
    fun fromControllerColumns(columns: ControllerColumns?): Int =
        columns?.count ?: ControllerColumns.TWO.count

    @TypeConverter
    fun toControllerColumns(count: Int?): ControllerColumns =
        ControllerColumns.values().find { it.count == count } ?: ControllerColumns.TWO

    @TypeConverter
    fun fromWidgetType(type: WidgetType?): Int =
        type?.ordinal ?: WidgetType.EMPTY.ordinal

    @TypeConverter
    fun toWidgetType(ordinal: Int?): WidgetType =
        ordinal?.let { WidgetType.values().getOrNull(it) } ?: WidgetType.EMPTY

    @TypeConverter
    fun fromWidgetIcon(icon: WidgetIcon?): Int = icon?.id ?: WidgetIcon.NO_ICON.id

    @TypeConverter
    fun toWidgetIcon(iconId: Int?): WidgetIcon =
        WidgetIcon.values().find { it.id == iconId } ?: WidgetIcon.NO_ICON

    @TypeConverter
    fun fromWidgetSize(size: WidgetSize?): Int = size?.span ?: WidgetSize.SMALL.span

    @TypeConverter
    fun toWidgetSize(span: Int?): WidgetSize =
        WidgetSize.values().find { it.span == span } ?: WidgetSize.SMALL
}