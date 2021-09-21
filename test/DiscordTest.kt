package com.ntihs_fk

import com.google.gson.Gson
import com.ntihs_fk.socialSoftware.discord.DiscordConfig
import com.ntihs_fk.socialSoftware.discord.discordPost
import kotlinx.coroutines.DelicateCoroutinesApi
import org.junit.Test
import java.io.File

class DiscordTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun discordVoteTest() {
        val classloader: ClassLoader = Thread.currentThread().contextClassLoader
        val discordConfigJSONFileUrl =
            classloader.getResource("DiscordWebhook/config.json") ?: throw Error("No DiscordWebhook/config.json")

        val discordConfigJSONString = File(discordConfigJSONFileUrl.toURI()).readText()
        val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)
        discordPost(discordConfig.voteChannelWebhook, "aasdasdasa", "a", 4)
    }
}