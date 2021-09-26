package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.data.GmailConfig
import com.ntihs_fk.data.TwitterConfig
import java.io.File

class Config {
    companion object {
        // read config json files
        private val discordConfigJSONString = File("./Discord.config.json").readText()
        private val gmailConfigJSONString = File("./Gmail.config.json").readText()
        private val twitterConfigJSONString = File("./Twitter.config.json").readText()

        private val gson = Gson()

        var port: Int = System.getenv("PORT").toInt()
        var domain = System.getenv("DOMAIN") ?: "127.0.0.1:$port"

        var ssl = System.getenv("SSL").toBoolean()

        // JWT config
        val secret = System.getenv("jwt_secret") ?: "secret"
        val issuer = "http${if (ssl) "s" else ""}://$domain"
        val audience = "$issuer/vote"
        const val expiresAt = 60000 * 60 * 24 * 7

        // Discord config
        val discordConfig: DiscordConfig = gson.fromJson(discordConfigJSONString, DiscordConfig::class.java)

        // Gmail config
        val gmailConfig: GmailConfig = gson.fromJson(gmailConfigJSONString, GmailConfig::class.java)

        // Twitter config
        val twitterConfig: TwitterConfig = gson.fromJson(twitterConfigJSONString, TwitterConfig::class.java)
    }
}
