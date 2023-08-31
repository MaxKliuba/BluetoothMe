package com.android.maxclub.bluetoothme.feature.controllers.domain.models

import java.util.UUID

sealed class Widget(
    val id: UUID,
    val controllerId: UUID,
    val messageTag: String,
    val title: String,
    val size: WidgetSize,
    val readOnly: Boolean,
    val position: Int,
) {

    abstract val value: String

    abstract fun setStateFromValue(value: String)

    abstract fun copy(
        readOnly: Boolean = this.readOnly,
        position: Int = this.position
    ): Widget

    class Empty(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
        size: WidgetSize,
        position: Int,
    ) : Widget(id, controllerId, "", "", size, true, position) {

        override val value: String
            get() = ""

        override fun setStateFromValue(value: String) {
        }

        override fun copy(readOnly: Boolean, position: Int): Widget =
            Empty(id, controllerId, size, position)
    }

    class Button(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
        messageTag: String,
        title: String,
        size: WidgetSize,
        readOnly: Boolean,
        position: Int,
        state: Boolean = false,
    ) : Widget(id, controllerId, messageTag, title, size, readOnly, position) {

        var state: Boolean = state
            private set

        override val value: String
            get() = if (state) "1" else "0"

        override fun setStateFromValue(value: String) {
            state = value != "0"
        }

        override fun copy(readOnly: Boolean, position: Int): Widget =
            Button(id, controllerId, messageTag, title, size, readOnly, position, state)
    }
}