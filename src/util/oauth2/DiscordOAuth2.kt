package com.ntihs_fk.util.oauth2

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.ntihs_fk.data.DiscordUserData
import com.ntihs_fk.util.Config
import io.ktor.features.*

class DiscordOAuth2 : OAuth2() {
    private val discordAPIUrl = "https://discord.com/api/v8"

    private fun requestForm(formData: Map<String, Any>): AccessTokenResponseData {
        val response = HttpRequest.post("$discordAPIUrl/oauth2/token")
            .basic(Config.discordConfig.discord_id, Config.discordConfig.discord_secret)
            .form(formData)

        if (!response.ok()) throw BadRequestException("Discord OAuth2 error")

        return gson.fromJson(response.body(), AccessTokenResponseData::class.java)
    }

    fun exchangeCode(code: String): AccessTokenResponseData {
        val data = Data(
            client_id = Config.discordConfig.discord_id,
            client_secret = Config.discordConfig.discord_secret,
            grant_type = "authorization_code",
            code = code,
            redirect_uri = "${Config.issuer}/api/discord/authorize"
        )

        return requestForm(data.serializeToMap())
    }

    fun getUserinfoData(accessToken: String): DiscordUserData {
        val response = HttpRequest.get("https://discord.com/api/v8/users/@me")
            .header("Authorization", "Bearer $accessToken")

        if (!response.ok()) throw BadRequestException("Discord authorization error")

        val userDataJsonString = response.body()

        return Gson().fromJson(userDataJsonString, DiscordUserData::class.java)
    }

//        fun refreshToken(refresh_token: String): AccessTokenResponse {
//            val data = gson.toJson(
//                Data(
//                    client_id = Config.discordConfig.discord_id,
//                    client_secret = Config.discordConfig.discord_secret,
//                    grant_type = "refresh_token",
//                    refresh_token = refresh_token
//                )
//            )
//
//            return requestForm(data.serializeToMap())
//        }
}