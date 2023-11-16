package com.android.maxclub.bluetoothme

import com.android.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class MessageTest {

    @Test
    fun toMessageString_MessageWithTagAngValue_ReturnsStringWithTagAndTagTerminatorAndValue() {
        val expectedStringMessage = "tag/value"
        val message = Message(
            type = Message.Type.Output,
            tag = "tag",
            value = "value",
            timestamp = Date().time,
        )

        val actualStringMessage = message.toMessageString()

        assertEquals(expectedStringMessage, actualStringMessage)
    }

    @Test
    fun toMessageString_MessageWithEmptyTag_ReturnsStringWithValue() {
        val expectedStringMessage = "value"
        val message = Message(
            type = Message.Type.Output,
            tag = "",
            value = "value",
            timestamp = Date().time,
        )

        val actualStringMessage = message.toMessageString()

        assertEquals(expectedStringMessage, actualStringMessage)
    }

    @Test
    fun toMessageString_MessageWithEmptyValue_ReturnsStringWithTagAndTagTerminator() {
        val expectedStringMessage = "tag/"
        val message = Message(
            type = Message.Type.Output,
            tag = "tag",
            value = "",
            timestamp = Date().time,
        )

        val actualStringMessage = message.toMessageString()

        assertEquals(expectedStringMessage, actualStringMessage)
    }

    @Test
    fun toMessageString_MessageWithEmptyTagAngValue_ReturnsEmptyString() {
        val expectedStringMessage = ""
        val message = Message(
            type = Message.Type.Output,
            tag = "",
            value = "",
            timestamp = Date().time,
        )

        val actualStringMessage = message.toMessageString()

        assertEquals(expectedStringMessage, actualStringMessage)
    }
}