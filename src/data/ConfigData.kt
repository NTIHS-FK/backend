package com.ntihs_fk.data

data class ConfigData(
    val google: GoogleConfigData,
    val discord: DiscordConfigData,
    val admin: AdminConfigData,
    val twitter: TwitterConfigData,
    val gmail: GmailConfigData
)
