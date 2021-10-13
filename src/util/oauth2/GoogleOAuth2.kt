package com.ntihs_fk.util.oauth2

import com.github.kevinsawicki.http.HttpRequest
import com.ntihs_fk.util.Config
import data.GoogleUserData
import io.ktor.features.*

class GoogleOAuth2 : OAuth2() {

    private fun requestForm(formData: Map<String, Any>): AccessTokenResponseData {
        val response = HttpRequest.post(Config.googleConfig.token_uri)
            .basic(Config.googleConfig.client_id, Config.googleConfig.client_secret)
            .form(formData)

        if (!response.ok()) throw BadRequestException("Google OAuth2 error")

        return gson.fromJson(response.body(), AccessTokenResponseData::class.java)
    }

    fun exchangeCode(code: String): AccessTokenResponseData {
        val data = Data(
            Config.googleConfig.client_id,
            Config.googleConfig.client_secret,
            "authorization_code",
            code,
            "${Config.issuer}/api/google/authorize"
        )

        return requestForm(data.serializeToMap())
    }

    fun getUserinfoProfile(access_token: String): GoogleUserData {
        val response = HttpRequest.get("https://www.googleapis.com/oauth2/v1/userinfo?alt=json")
            .header("Authorization", "Bearer $access_token")

        if (!response.ok()) throw BadRequestException("Google authorization error")

        return gson.fromJson(response.body(), GoogleUserData::class.java)
    }
}