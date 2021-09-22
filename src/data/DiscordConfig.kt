package com.ntihs_fk.data

data class DiscordConfig(
    val voteChannelWebhook: String,
    val postChannelWebhook: String,
    val discord_id: String,
    val discord_secret: String
)