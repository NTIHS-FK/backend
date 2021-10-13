package com.ntihs_fk.router.loginSystem

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.LoginTokenData
import com.ntihs_fk.database.DiscordOAuth2Table
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.oauth2.DiscordOAuth2
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.discordOAuth2() {
    get("/api/discord/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val discordOAuth2 = DiscordOAuth2()
        val data = discordOAuth2.exchangeCode(code)
        val userData = discordOAuth2.getUserinfoData(data.access_token)

        val token = JWT.create()
            .withIssuer(Config.issuer)
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("username", "${userData.username}#${userData.discriminator}")
            .withClaim("avatar", userData.avatar)
            .withClaim("verify", true)
            .withClaim("admin", false)
            .withClaim("type", "discord")
            .withExpiresAt(Date(System.currentTimeMillis() + Config.expiresAt))
            .sign(Algorithm.HMAC256(Config.secret))

        transaction {
            DiscordOAuth2Table.insertIgnore {
                it[id] = userData.id
                it[email] = userData.email
            }
        }

        call.sessions.set(LoginTokenData(token))
        call.respondRedirect("/")
    }

    get("/auth/discord") {
        call.respondRedirect(
            "https://discord.com/api/oauth2/authorize?" +
                "client_id=${Config.discordConfig.discord_id}&" +
                "redirect_uri=${Config.issuer}/api/discord/authorize&" +
                "response_type=code&" +
                "scope=identify%20email"
        )
    }
}