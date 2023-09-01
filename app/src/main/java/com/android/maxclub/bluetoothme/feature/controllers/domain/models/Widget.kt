package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import java.util.UUID

sealed class Widget(
    val id: UUID,
    val controllerId: UUID,
    val messageTag: String,
    val title: String,
    val size: WidgetSize,
    val enabled: Boolean,
    val position: Int,
) {

    abstract val messageValue: String

    abstract fun copyWithState(stateValue: String): Widget

    fun copy(
        id: UUID = this.id,
        controllerId: UUID = this.controllerId,
        messageTag: String = this.messageTag,
        title: String = this.title,
        size: WidgetSize = this.size,
        enabled: Boolean = this.enabled,
        position: Int = this.position
    ): Widget = when (this) {
        is Empty -> Empty(id, controllerId, size, position)
        is Button -> Button(id, controllerId, messageTag, title, size, enabled, position, state)
    }


    class Empty(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
        size: WidgetSize = WidgetSize.SMALL,
        position: Int = -1,
    ) : Widget(id, controllerId, "", "", size, false, position) {

        override val messageValue: String
            get() = ""

        override fun copyWithState(stateValue: String) = this
    }

    class Button(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
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