package com.ntihs_fk.router.loginSystem

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.Login
import com.ntihs_fk.functions.Config
import com.ntihs_fk.functions.GoogleOAuth2
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.util.*

fun Route.googleOAuth2() {
    get("/api/google/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val accessToken = GoogleOAuth2.exchangeCode(code).access_token
        val userData = GoogleOAuth2.getUserinfoProfile(accessToken)
        val token = JWT.create()
            .withIssuer(Config.issuer)
            .withAudience(Config.audience)
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("username", userData.name)
            .withClaim("avatar", userData.picture)
            .withClaim("verify", true)
            .withClaim("type", "google")
            .withExpiresAt(Date(System.currentTimeMillis() + Config.expiresAt))
            .sign(Algorithm.HMAC256(Config.secret))

        call.sessions.set(Login(token))
        call.respondRedirect("/")
    }

    get("/auth/google") {
        call.respondRedirect(
            "${Config.googleConfig.auth_uri}?" +
                    "client_id=${Config.googleConfig.client_id}&" +
                    "response_type=code&" +
                    "scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email&" +
                    "redirect_uri=${Config.issuer}/api/google/authorize"
        )
    }
}