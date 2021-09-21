package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.socialSoftware.discord.DiscordConfig
import java.io.File

private val classloader: ClassLoader = Thread.currentThread().contextClassLoader
private val discordConfigJSONFileUrl =
    classloader.getResource("DiscordWebhook/config.json") ?: throw Error("No DiscordWebhook/config.json")
private val discordConfigJSONString = File(discordConfigJSONFileUrl.toURI()).readText()
val discordConfig: DiscordConfig = Gson().fromJson(discordConfigJSONString, DiscordConfig::class.java)
val secret = System.getenv("jwt_secret") ?: "secret"

const val domain = "127.0.0.1:8080"
const val ssl = false
val issuer = "http${if (ssl) "s" else ""}://$domain"
val audience = "$issuer/vote"
