package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.maxclub.bluetoothme.R

enum class WidgetIcon(
    val id: Int,
    @DrawableRes val drawableResId: Int,
    @StringRes val titleResId: Int,
) {
    NO_ICON(0, 0, R.string.widget_icons__no_icon),
    ARROW_UP(1, R.drawable.widget_icons__arrow_up, R.string.widget_icons__arrow_up),
    ARROW_DOWN(2, R.drawable.widget_icons__arrow_down, R.string.widget_icons__arrow_down),
    ARROW_LEFT(3, R.drawable.widget_icons__arrow_left, R.string.widget_icons__arrow_left),
    ARROW_RIGHT(4, R.drawable.widget_icons__arrow_right, R.string.widget_icons__arrow_right),
    ROTATE_LEFT(5, R.drawable.widget_icons__rotate_left, R.string.widget_icons__rotate_left),
    ROTATE_RIGHT(6, R.drawable.widget_icons__rotate_right, R.string.widget_icons__rotate_right),
    STOP(7, R.drawable.widget_icons__stop, R.string.widget_icons__stop),
    SPEED(8, R.drawable.widget_icons__speed, R.string.widget_icons__speed),
    BATTERY(9, R.drawable.widget_icons__battery, R.string.widget_icons__battery),
    HIGHLIGHT(10, R.drawable.widget_icons__highlight, R.string.widget_icons__highlight),
    LIGHT(11, R.drawable.widget_icons__lightbulb, R.string.widget_icons__lightbulb),
    POWER(12, R.drawable.widget_icons__power, R.string.widget_icons__power),
    COLOR(13, R.drawable.widget_icons__color, R.string.widget_icons__color),
    TEMP(14, R.drawable.widget_icons__temperature, R.string.widget_icons__temperature),
    VOLUME(15, R.drawable.widget_icons__volume, R.string.widget_icons__volume),
    TIME(16, R.drawable.widget_icons__time, R.string.widget_icons__time),
    CAMERA(17, R.drawable.widget_icons__camera, R.string.widget_icons__camera),
    EFFECT(18, R.drawable.widget_icons__effect, R.string.widget_icons__effect),
    LOCATION(19, R.drawable.widget_icons__location, R.string.widget_icons__location),
    LOCK(20, R.drawable.widget_icons__lock, R.string.widget_icons__lock),
    SAVE(21, R.drawable.widget_icons__save, R.string.widget_icons__save),
    SPECIAL(22, R.drawable.widget_icons__special, R.string.widget_icons__special),
    ANDROID(23, R.drawable.widget_icons__android, R.string.widget_icons__android);

    val isValid: Boolean
        get() = drawableResId != 0
}