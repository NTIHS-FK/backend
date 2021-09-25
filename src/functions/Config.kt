package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.DiscordConfig
import java.io.File

class Config {
    companion object {
        private val classloader: ClassLoader = Thread.currentThread().contextClassLoader
        private val discordConfigJSONFileUrl =
            classloader.getResource("DiscordWebhook/config.json") ?: throw Error("No DiscordWebhook/config.json")
        private val discordConfigJSONString = File(discordConfigJSONFileUrl.toURI()).readText()
        val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)
        val secret = System.getenv("jwt_secret") ?: "secret"

        private val domain = System.getenv("domain") ?: "127.0.0.1:8080"
        private val ssl = System.getenv("ssl").toBoolean()

        val issuer = "http${if (ssl) "s" else ""}://$domain"
        val audience = "$issuer/vote"
        val expiresAt = 60000 * 60 * 24 * 7
    }
}
