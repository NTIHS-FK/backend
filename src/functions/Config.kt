package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.data.GmailConfig
import com.ntihs_fk.data.TwitterConfig
import java.io.File

class Config {
    companion object {
        // read config json files
        var discordConfigFile = File("./Discord.config.json")
        var gmailConfigFile = File("./Gmail.config.json")
        var twitterConfigFile = File("./Twitter.config.json")

        private val gson = Gson()

        var port = System.getenv("PORT") ?: 8080
        var domain = System.getenv("DOMAIN") ?: "127.0.0.1:8080"

        var ssl = System.getenv("SSL").toBoolean()

        // JWT config
        val secret = System.getenv("jwt_secret") ?: "secret"
        val issuer = "http${if (ssl) "s" else ""}://$domain"
        val audience = "$issuer/vote"
        const val expiresAt = 60000 * 60 * 24 * 7

        // Discord config
        val discordConfig: DiscordConfig = gson.fromJson(discordConfigFile.readText(), DiscordConfig::class.java)

        // Gmail config
        val gmailConfig: GmailConfig = gson.fromJson(gmailConfigFile.readText(), GmailConfig::class.java)

        // Twitter config
        val twitterConfig: TwitterConfig = gson.fromJson(twitterConfigFile.readText(), TwitterConfig::class.java)
    }
}
