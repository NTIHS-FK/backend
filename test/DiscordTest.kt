package com.ntihs_fk

import com.ntihs_fk.util.websocketServer
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DiscordTest {
    @Test
    fun testPostConversation() {
        withTestApplication(Application::module) {
            handleWebSocketConversation("/post/broadcast") { incoming, _ ->
                var greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("ok", greetingText)
                websocketServer.broadcast(mapOf("a" to "b"))
                greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("{\"a\":\"b\"}", greetingText)
            }
        }
    }
}