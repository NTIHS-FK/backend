package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import com.ntihs_fk.data.GmailConfig
import java.io.File

class Config {
    companion object {
        // read config json files
        private val discordConfigJSONString = File("./Discord.config.json").readText()
        private val gmailConfigJSONString = File("./Gmail.config.json").readText()
        // Discord config
        val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)

        private val domain = System.getenv("DOMAIN") ?: "127.0.0.1:8080"
        val ssl = System.getenv("SSL").toBoolean()
        // JWT config
        val secret = System.getenv("jwt_secret") ?: "secret"
        val issuer = "http${if (ssl) "s" else ""}://$domain"
        val audience = "$issuer/vote"
        const val expiresAt = 60000 * 60 * 24 * 7
        // Gmail config
        val gmailConfig: GmailConfig = Gson().fromJson(gmailConfigJSONString, GmailConfig::class.java)

    }
}
