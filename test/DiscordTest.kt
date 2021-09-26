package com.ntihs_fk

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.socialSoftware.discord.discordPost
import kotlinx.coroutines.DelicateCoroutinesApi
import org.junit.Test
import java.io.File

class DiscordTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun discordVoteTest() {


        val discordConfigJSONString = File("./Discord.config.json").readText()
        val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)
        discordPost(discordConfig.voteChannelWebhook, "aasdasdasa", "a", 4)
    }
}