package com.android.maxclub.bluetoothme.feature.controllers.domain.models

sealed class Widget(
    val id: Int,
    val controllerId: Int,
    val messageTag: String,
    val title: String,
    val icon: WidgetIcon,
    val size: WidgetSize,
    val enabled: Boolean,
    val position: Int,
) {

    abstract val messageValue: String

    abstract fun copyWithState(stateValue: String): Widget

    fun copy(
        id: Int = this.id,
        controllerId: Int = this.controllerId,
        messageTag: String = this.messageTag,
        title: String = this.title,
        icon: WidgetIcon = this.icon,
        size: WidgetSize = this.size,
        enabled: Boolean = this.enabled,
        position: Int = this.position
    ): Widget = when (this) {
        is Empty -> Empty(id, controllerId, icon, size, position)
        is Button -> Button(
            id, controllerId, messageTag, title, icon, size, enabled, position,
            state,
        )
    }

    class Empty(
        id: Int = 0,
        controllerId: Int,
        icon: WidgetIcon = WidgetIcon.NO_ICON,
        size: WidgetSize = WidgetSize.SMALL,
        position: Int = -1,
    ) : Widget(id, controllerId, "", "", icon, size, false, position) {

        override val messageValue: String
            get() = ""

        override fun copyWithState(stateValue: String) = this
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
        val state: Boolean = false,
    ) : Widget(id, controllerId, messageTag, title, icon, size, enabled, position) {

        override val messageValue: String
            get() = if (state) "1" else "0"

        override fun copyWithState(stateValue: String): Widget =
            Button(
                id, controllerId, messageTag, title, icon, size, enabled, position,
                state = stateValue != "0"
            )
    }
}