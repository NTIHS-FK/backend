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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.discord() {
    get("/api/discord/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val data = DiscordOAuth2.exchangeCode(code)
        val response = HttpRequest.get("https://discord.com/api/v8/users/@me")
            .header("Authorization", "Bearer ${data.access_token}")

        if (!response.ok()) throw BadRequestException("Discord authorization error")

        val userDataJsonString = response.body()
        val userData = Gson().fromJson(userDataJsonString, DiscordUserData::class.java)

        val token = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", "${userData.username}#${userData.discriminator}")
            .withClaim("avatar", userData.avatar)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))

        transaction {
            DiscordOAuth2Table.insert {
                it[id] = userData.id
                it[email] = userData.email
            }
        }

        call.sessions.set(Login(token))
        call.respond(
            apiFrameworkFun(
                hashMapOf(
                    "token" to token
                )
            )
        )
    }
}