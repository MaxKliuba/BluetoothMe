package com.tech.maxclub.bluetoothme

import com.tech.maxclub.bluetoothme.feature.bluetooth.data.mappers.toMessage
import com.tech.maxclub.bluetoothme.feature.bluetooth.domain.messages.Message
import org.junit.Assert.*
import org.junit.Test

class MessageMappersKtTest {

    @Test
    fun stringToMessage_MessageStringWithTagAndValue_ReturnsMessageWithTagAndValue() {
        val messageString = "tag/value"

        val message = messageString.toMessage(Message.Type.Output)

        assertEquals("tag", message.tag)
        assertEquals("value", message.value)
    }

    @Test
    fun stringToMessage_MessageStringWithoutTag_ReturnsMessageWithEmptyTag() {
        val messageString = "value"

        val message = messageString.toMessage(Message.Type.Output)

        assertEquals("", message.tag)
        assertEquals("value", message.value)
    }

    @Test
    fun stringToMessage_MessageStringWithEmptyTag_ReturnsMessageWithEmptyTag() {
        val messageString = "/value"

        val message = messageString.toMessage(Message.Type.Output)

        assertEquals("", message.tag)
        assertEquals("value", message.value)
    }

    @Test
    fun stringToMessage_MessageStringWithEmptyValue_ReturnsMessageWithEmptyValue() {
        val messageString = "tag/"

        val message = messageString.toMessage(Message.Type.Output)

        assertEquals("tag", message.tag)
        assertEquals("", message.value)
    }

    @Test
    fun stringToMessage_MessageStringWithMultipleTags_ReturnsMessageWithTagAndValue() {
        val messageString = "tag/test/value"

        val message = messageString.toMessage(Message.Type.Output)

        assertEquals("tag", message.tag)
        assertEquals("test/value", message.value)
    }
}