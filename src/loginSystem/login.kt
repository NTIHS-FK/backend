package com.ntihs_fk.loginSystem

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.functions.apiFrameworkFun
import com.ntihs_fk.functions.domain
import com.ntihs_fk.functions.ssl
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

data class User(val username: String)

fun Route.login(testing: Boolean) {
    val secret = if (testing)
        "secret"
    else
        System.getenv("jwt_secret") ?: "secret"

    post("/api/login") {
        val user = call.receive<User>()
        val token = JWT.create()
            .withIssuer("http${if (ssl) "s" else ""}://$domain")
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))

        call.respond(apiFrameworkFun(hashMapOf("token" to token)))
    }
}