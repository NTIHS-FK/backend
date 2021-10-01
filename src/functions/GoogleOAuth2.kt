package com.ntihs_fk.functions

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.features.*

class GoogleOAuth2 {
    private val gson = Gson()

    private data class Data(
        val client_id: String,
        val client_secret: String,
        val grant_type: String,
        val code: String? = null,
        val redirect_uri: String? = null
    )

    data class AccessTokenResponseData(
        val access_token: String
    )

    data class UserData(
        val name: String,
        val picture: String,
        val email: String
    )

    private inline fun <I, reified O> I.convert(): O {
        val json = DiscordOAuth2.gson.toJson(this)
        return DiscordOAuth2.gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    private fun <T> T.serializeToMap(): Map<String, Any> = convert()

    private fun requestForm(formData: Map<String, Any>): AccessTokenResponseData {
        val response = HttpRequest.post(Config.googleConfig.token_uri)
            .basic(Config.googleConfig.client_id, Config.googleConfig.client_secret)
            .form(formData)

        if (!response.ok()) throw BadRequestException("Google OAuth2 error")
        val body = response.body()
        println(body)
        return gson.fromJson(body, AccessTokenResponseData::class.java)
    }

    fun exchangeCode(code: String): AccessTokenResponseData {
        val data = Data(
            Config.googleConfig.client_id,
            Config.googleConfig.client_secret,
            "authorization_code",
            code,
            Config.googleConfig.redirect_uri
        )

        return requestForm(data.serializeToMap())
    }

    fun getUserinfoProfile(access_token: String): UserData {
        val response = HttpRequest.get("https://www.googleapis.com/oauth2/v1/userinfo?alt=json")
            .header("Authorization", "Bearer $access_token")

        if (!response.ok()) throw BadRequestException("Google authorization error")
        val userDataJsonString = response.body()
        println(userDataJsonString)
        return gson.fromJson(userDataJsonString, UserData::class.java)
    }
}