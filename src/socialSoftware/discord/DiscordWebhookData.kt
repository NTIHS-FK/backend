package com.ntihs_fk.socialSoftware.discord

data class DiscordWebhookData(
    val username: String,
//    val avatar_url: String = "https://cdn.discordapp.com/avatars/889564072088068197/195b80329d0cd6eb999fd6ee2f5b34f6.webp?size=128",
    val content: String = "",
    val embeds: List<DiscordWebhookEmbed>? = null
)

data class DiscordWebhookEmbed(
    val author: Author,
    val image: Image,
    val title: String,
    val url: String,
    val description: String,
    val color: Int,
    val footer: Footer
)

data class Author(
    val name: String,
    val url: String,
    val icon_url: String
)

data class Image(
    val url: String
)

data class Footer(
    val text: String,
    val icon_url: String
)