package com.ntihs_fk.router.loginSystem

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.LoginTokenData
import com.ntihs_fk.database.PrivateClaims
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.oauth2.GoogleOAuth2
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.googleOAuth2() {
    get("/api/google/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val googleOAuth2 = GoogleOAuth2()
        val accessToken = googleOAuth2.exchangeCode(code).access_token
        val userData = googleOAuth2.getUserinfoProfile(accessToken)
        val jwtID = UUID.randomUUID().toString()
        val token = JWT.create()
            .withIssuer(Config.issuer)
            .withJWTId(jwtID)
            .withClaim("username", userData.name)
            .withClaim("avatar", userData.picture)
            .withClaim("verify", userData.verified_email)
            .withClaim("admin", false)
            .withClaim("type", "google")
            .withExpiresAt(Date(System.currentTimeMillis() + Config.expiresAt))
            .sign(Algorithm.HMAC256(Config.secret))

        transaction {
            PrivateClaims.insert {
                it[this.email] = userData.email
                it[this.jwtID] = jwtID
            }
        }

        call.sessions.set(LoginTokenData(token))
        call.respondRedirect("/")
    }

    get("/auth/google") {
        call.respondRedirect(
            "${Config.googleConfig.auth_uri}?" +
                    "client_id=${Config.googleConfig.client_id}&" +
                    "response_type=code&" +
                    "scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email&" +
                    "redirect_uri=${Config.issuer}/api/google/authorize"
        )
    }
}