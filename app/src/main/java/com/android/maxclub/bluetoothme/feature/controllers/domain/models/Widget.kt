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

    abstract val messageValue: String

    abstract fun copyWithState(stateValue: String): Widget

    class Empty(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
        size: WidgetSize,
        position: Int = -1,
    ) : Widget(id, controllerId, "", "", size, true, position) {

        override val messageValue: String
            get() = ""

        override fun copyWithState(stateValue: String) = this
    }

    class Button(
        id: UUID = UUID.randomUUID(),
        controllerId: UUID,
        messageTag: String,
        title: String,
        size: WidgetSize,
        readOnly: Boolean,
        position: Int = -1,
        val state: Boolean = false,
    ) : Widget(id, controllerId, messageTag, title, size, readOnly, position) {

        override val messageValue: String
            get() = if (state) "1" else "0"

        override fun copyWithState(stateValue: String): Widget =
            Button(
                id, controllerId, messageTag, title, size, readOnly, position,
                state = stateValue != "0"
            )
    }
}