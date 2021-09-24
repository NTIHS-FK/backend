package com.ntihs_fk.data

data class DiscordUserData(
    val id: Long,
    val username: String,
    val avatar: String,
    val discriminator: Int,
    val email: String,
    val verified: String
)
