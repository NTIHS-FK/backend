package com.ntihs_fk.functions

import com.ntihs_fk.data.*

class Config {
    companion object {

        var port = System.getenv("PORT") ?: 8080
        var domain = System.getenv("DOMAIN") ?: "127.0.0.1:8080"

        var ssl = false

        // JWT config
        val secret = System.getenv("jwt_secret") ?: "secret"
        val issuer = "http${if (ssl) "s" else ""}://$domain"
        const val expiresAt = 60000 * 60 * 24 * 7

        // Discord config
        lateinit var discordConfig: DiscordConfigData

        // Gmail config
        lateinit var gmailConfig: GmailConfigData

        // Twitter config
        lateinit var twitterConfig: TwitterConfigData

        // Google config
        lateinit var googleConfig: GoogleConfigData

        // Admin config
        lateinit var adminConfig: AdminConfigData
    }
}
