package com.ntihs_fk.functions

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.features.*

class DiscordOAuth2 {
    companion object {

        private const val discordAPIUrl = "https://discord.com/api/v8"
        private const val redirect_uri = "http://127.0.0.1:8080/api/discord/authorize"
        private val gson = Gson()

        private data class Data(
            val client_id: String,
            val client_secret: String,
            val grant_type: String,
            val code: String? = null,
            val redirect_uri: String? = null,
            val refresh_token: String? = null
        )

        data class AccessTokenResponse(
            val access_token: String,
            val token_type: String,
            val expires_in: Long,
            val refresh_token: String,
            val scope: String
        )

        private inline fun <I, reified O> I.convert(): O {
            val json = gson.toJson(this)
            return gson.fromJson(json, object : TypeToken<O>() {}.type)
        }

        private fun <T> T.serializeToMap(): Map<String, Any> = convert()

        private fun requestForm(formData: Map<String, Any>): AccessTokenResponse {
            val response = HttpRequest.post("$discordAPIUrl/oauth2/token")
                .basic(Config.discordConfig.discord_id, Config.discordConfig.discord_secret)
                .form(formData)

            if (!response.ok()) throw BadRequestException("Discord OAuth2 error")

            return gson.fromJson(response.body(), AccessTokenResponse::class.java)
        }

        fun exchangeCode(code: String): AccessTokenResponse {
            val data = Data(
                client_id = Config.discordConfig.discord_id,
                client_secret = Config.discordConfig.discord_secret,
                grant_type = "authorization_code",
                code = code,
                redirect_uri = redirect_uri
            )

            return requestForm(data.serializeToMap())
        }

        fun refreshToken(refresh_token: String): AccessTokenResponse {
            val data = gson.toJson(
                Data(
                    client_id = Config.discordConfig.discord_id,
                    client_secret = Config.discordConfig.discord_secret,
                    grant_type = "refresh_token",
                    refresh_token = refresh_token
                )
            )

            return requestForm(data.serializeToMap())
        }
    }
}