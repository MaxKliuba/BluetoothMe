package com.tech.maxclub.bluetoothme.feature.controllers.domain.models

import kotlin.reflect.KClass

sealed class Widget<T>(
    val id: Int,
    val controllerId: Int,
    val messageTag: String,
    val title: String,
    val icon: WidgetIcon,
    val size: WidgetSize,
    val enabled: Boolean,
    val position: Int,
    val state: T,
) {
    abstract fun convertStateToMessageValue(state: T): String

    abstract fun copyWithState(stateValue: String): Widget<T>

    inline fun <reified T : Widget<*>> asType(type: KClass<T>): T =
        when (type) {
            Button::class ->
                Button(id, controllerId, messageTag, title, icon, size, enabled, position) as T

            Switch::class ->
                Switch(id, controllerId, messageTag, title, icon, size, enabled, position) as T

            Slider::class ->
                Slider(id, controllerId, messageTag, title, icon, size, enabled, position) as T

            Text::class ->
                Text(id, controllerId, messageTag, title, icon, size, enabled, position) as T

            else -> Empty(id, controllerId, icon, size, position) as T
        }

    fun copy(
        id: Int = this.id,
        controllerId: Int = this.controllerId,
        messageTag: String = this.messageTag,
        title: String = this.title,
        icon: WidgetIcon = this.icon,
        size: WidgetSize = this.size,
        enabled: Boolean = this.enabled,
        position: Int = this.position,
    ): Widget<*> = when (this) {
        is Empty -> Empty(id, controllerId, icon, size, position)

        is Button -> Button(
            id, controllerId, messageTag, title, icon, size, enabled, position, this.state,
        )

        is Switch -> Switch(
            id, controllerId, messageTag, title, icon, size, enabled, position, this.state,
        )

        is Slider -> Slider(
            id, controllerId, messageTag, title, icon, size, enabled, position, this.state,
            this.minValue, this.maxValue, this.step,
        )

        is Text -> Text(
            id, controllerId, messageTag, title, icon, size, enabled, position, this.state,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Widget<*>

        if (id != other.id) return false
        if (controllerId != other.controllerId) return false
        if (messageTag != other.messageTag) return false
        if (title != other.title) return false
        if (icon != other.icon) return false
        if (size != other.size) return false
        if (enabled != other.enabled) return false
        if (position != other.position) return false
        return state == other.state
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + controllerId
        result = 31 * result + messageTag.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + position
        result = 31 * result + (state?.hashCode() ?: 0)
        return result
    }


    class Empty(
        id: Int = 0,
        controllerId: Int,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        position: Int = -1,
    ) : Widget<Unit>(id, controllerId, "", "", icon, size, true, position, Unit) {

        override fun convertStateToMessageValue(state: Unit): String = ""

        override fun copyWithState(stateValue: String): Empty = this
    }

    class Button(
        id: Int = 0,
        controllerId: Int,
        messageTag: String,
        title: String,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        enabled: Boolean = true,
        position: Int = -1,
        state: Boolean = false,
    ) : Widget<Boolean>(id, controllerId, messageTag, title, icon, size, enabled, position, state) {

        override fun convertStateToMessageValue(state: Boolean): String =
            if (state) "1" else "0"

        override fun copyWithState(stateValue: String): Button =
            Button(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue.isNotEmpty() && stateValue != "0"
            )
    }

    class Switch(
        id: Int = 0,
        controllerId: Int,
        messageTag: String,
        title: String,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        enabled: Boolean = true,
        position: Int = -1,
        state: Boolean = false,
    ) : Widget<Boolean>(id, controllerId, messageTag, title, icon, size, enabled, position, state) {

        override fun convertStateToMessageValue(state: Boolean): String =
            if (state) "1" else "0"

        override fun copyWithState(stateValue: String): Switch =
            Switch(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue.isNotEmpty() && stateValue != "0"
            )
    }

    class Slider(
        id: Int = 0,
        controllerId: Int,
        messageTag: String,
        title: String,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        enabled: Boolean = true,
        position: Int = -1,
        state: Int = DEFAULT_MIN_VALUE,
        val minValue: Int = DEFAULT_MIN_VALUE,
        val maxValue: Int = DEFAULT_MAX_VALUE,
        val step: Int = DEFAULT_STEP,
    ) : Widget<Int>(id, controllerId, messageTag, title, icon, size, enabled, position, state) {

        override fun convertStateToMessageValue(state: Int): String = state.toString()

        override fun copyWithState(stateValue: String): Slider =
            Slider(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue.toIntOrNull()?.takeIf { it in minValue..maxValue } ?: minValue,
                minValue, maxValue, step,
            )

        fun copySliderParams(
            minValue: Int = this.minValue,
            maxValue: Int = this.maxValue,
            step: Int = this.step,
        ): Slider = Slider(
            id, controllerId, messageTag, title, icon, size, enabled, position, state,
            minValue = minValue,
            maxValue = maxValue,
            step = step,
        )

        fun incValue(): Int {
            val newValue = state.plus(step)

            return if (newValue in minValue..maxValue) newValue else state
        }

        fun decValue(): Int {
            val newValue = state.minus(step)

            return if (newValue in minValue..maxValue) newValue else state
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Slider

            if (minValue != other.minValue) return false
            if (maxValue != other.maxValue) return false
            return step == other.step
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + minValue
            result = 31 * result + maxValue
            result = 31 * result + step
            return result
        }

        companion object {
            const val MIN_VALUE = 0
            const val MAX_VALUE = 360
            const val MIN_STEP = 1

            const val DEFAULT_MIN_VALUE = MIN_VALUE
            const val DEFAULT_MAX_VALUE = MAX_VALUE
            const val DEFAULT_STEP = MIN_STEP
        }
    }

    class Text(
        id: Int = 0,
        controllerId: Int,
        messageTag: String,
        title: String,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        enabled: Boolean = true,
        position: Int = -1,
        state: String = "",
    ) : Widget<String>(id, controllerId, messageTag, title, icon, size, enabled, position, state) {

        override fun convertStateToMessageValue(state: String): String = state

        override fun copyWithState(stateValue: String): Text =
            Text(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue
            )
    }
}