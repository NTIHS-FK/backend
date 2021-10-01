package com.ntihs_fk.router.loginSystem

import com.ntihs_fk.functions.Config
import com.ntihs_fk.functions.GoogleOAuth2
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.googleOAuth2() {
    get("/api/google/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val googleOAuth2 = GoogleOAuth2()
        googleOAuth2.getUserinfoProfile(googleOAuth2.exchangeCode(code).access_token)
        call.respond("a")
    }

    get("/auth/google") {
        call.respondRedirect(
            "${Config.googleConfig.auth_uri}?" +
                    "client_id=${Config.googleConfig.client_id}&" +
                    "response_type=code&" +
                    "scope=https://www.googleapis.com/auth/userinfo.profile&" +
                    "redirect_uri=${Config.googleConfig.redirect_uri}"
        )
    }
}