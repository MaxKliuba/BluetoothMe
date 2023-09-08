package com.android.maxclub.bluetoothme.feature.controllers.domain.models

sealed class Widget(
    val id: Int,
    val controllerId: Int,
    val messageTag: String,
    val title: String,
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
        size: WidgetSize = this.size,
        enabled: Boolean = this.enabled,
        position: Int = this.position
    ): Widget = when (this) {
        is Empty -> Empty(id, controllerId, size, position)
        is Button -> Button(id, controllerId, messageTag, title, size, enabled, position, state)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Widget) return false

        if (id != other.id) return false
        if (controllerId != other.controllerId) return false
        if (messageTag != other.messageTag) return false
        if (title != other.title) return false
        if (size != other.size) return false
        if (enabled != other.enabled) return false
        if (position != other.position) return false
        if (messageValue != other.messageValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + controllerId.hashCode()
        result = 31 * result + messageTag.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + position
        result = 31 * result + messageValue.hashCode()
        return result
    }

    class Empty(
        id: Int = 0,
        controllerId: Int,
        size: WidgetSize = WidgetSize.SMALL,
        position: Int = -1,
    ) : Widget(id, controllerId, "", "", size, false, position) {

        override val messageValue: String
            get() = ""

        override fun copyWithState(stateValue: String) = this
    }

    class Button(
        id: Int = 0,
        controllerId: Int,
        messageTag: String,
        title: String,
        size: WidgetSize = WidgetSize.SMALL,
        enabled: Boolean = true,
        position: Int = -1,
        val state: Boolean = false,
    ) : Widget(id, controllerId, messageTag, title, size, enabled, position) {

        override val messageValue: String
            get() = if (state) "1" else "0"

        override fun copyWithState(stateValue: String): Widget =
            Button(
                id, controllerId, messageTag, title, size, enabled, position,
                state = stateValue != "0"
            )
    }
}