package com.ntihs_fk

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.functions.websocketServer
import com.ntihs_fk.socialSoftware.discord.discordPost
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import org.junit.Test
import java.io.File

class DiscordTest {
    @Test
    fun discordVoteTest() {
        val discordConfigJSONString = File("./Discord.config.json").readText()
        val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)
        discordPost(discordConfig.voteChannelWebhook, "aasdasdasa", "a", 4)
    }

    @Test
    fun testPostConversation() {
        withTestApplication(Application::module) {
            handleWebSocketConversation("/post/broadcast") { incoming, _ ->
                var greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("You are connected!", greetingText)
                websocketServer.broadcast(mapOf("a" to "b"))
                greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("{\"a\":\"b\"}", greetingText)
            }
        }
    }
}