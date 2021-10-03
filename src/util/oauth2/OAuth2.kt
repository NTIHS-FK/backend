package com.ntihs_fk.util.oauth2

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class OAuth2 {
    val gson = Gson()

    data class Data(
        val client_id: String,
        val client_secret: String,
        val grant_type: String,
        val code: String? = null,
        val redirect_uri: String? = null
    )

    data class AccessTokenResponseData(
        val access_token: String,
        val token_type: String,
        val expires_in: Long,
        val refresh_token: String?,
        val id_token: String?,
        val scope: String
    )

    private inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    fun Data.serializeToMap(): Map<String, Any> = convert()
}