package com.ntihs_fk.router.loginSystem

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.ntihs_fk.data.DiscordUserData
import com.ntihs_fk.data.Login
import com.ntihs_fk.database.DiscordOAuth2Table
import com.ntihs_fk.functions.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.discordOAuth2() {
    get("/api/discord/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val data = DiscordOAuth2.exchangeCode(code)
        val userData = DiscordOAuth2.getUserinfoData(data.access_token)

        val token = JWT.create()
            .withIssuer(Config.issuer)
            .withAudience(Config.audience)
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("username", "${userData.username}#${userData.discriminator}")
            .withClaim("avatar", userData.avatar)
            .withClaim("verify", true)
            .withClaim("type", "discord")
            .withExpiresAt(Date(System.currentTimeMillis() + Config.expiresAt))
            .sign(Algorithm.HMAC256(Config.secret))

        transaction {
            if (
                DiscordOAuth2Table.select {
                    DiscordOAuth2Table.id.eq(userData.id).and(
                        DiscordOAuth2Table.email.eq(userData.email)
                    )
                }.firstOrNull() == null
            )
                DiscordOAuth2Table.insert {
                    it[id] = userData.id
                    it[email] = userData.email
                }
        }

        call.sessions.set(Login(token))
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