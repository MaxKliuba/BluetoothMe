package com.android.maxclub.bluetoothme.feature.controllers.domain.models

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
        if (state != other.state) return false

        return true
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
    ) : Widget<Unit>(id, controllerId, "", "", icon, size, false, position, Unit) {

        override fun convertStateToMessageValue(state: Unit): String = ""

        override fun copyWithState(stateValue: String): Widget<Unit> = this
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

        override fun copyWithState(stateValue: String): Widget<Boolean> =
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

        override fun copyWithState(stateValue: String): Widget<Boolean> =
            Switch(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue.isNotEmpty() && stateValue != "0"
            )
    }
}