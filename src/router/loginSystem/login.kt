package com.ntihs_fk.router.loginSystem

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.database.UserTable
import com.ntihs_fk.error.UnauthorizedException
import com.ntihs_fk.functions.apiFrameworkFun
import com.ntihs_fk.functions.domain
import com.ntihs_fk.functions.ssl
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

data class User(val nameOrEmail: String, val password: String)

fun Route.login(testing: Boolean) {
    val secret = if (testing)
        "secret"
    else
        System.getenv("jwt_secret") ?: "secret"

    post("/api/login") {
        val user = call.receive<User>()
        var userData: ResultRow? = null

        transaction {
            userData = UserTable.select {
                UserTable.name.eq(user.nameOrEmail).or(
                    UserTable.email.eq(user.nameOrEmail)
                )
            }.firstOrNull()
        }

        if (userData == null) throw UnauthorizedException()

        val verify = BCrypt.verifyer()
            .verify(user.password.toCharArray(), userData!![UserTable.hashcode]).verified

        if (verify) {
            val token = JWT.create()
                .withIssuer("http${if (ssl) "s" else ""}://$domain")
                .withClaim("username", userData!![UserTable.name])
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(secret))

            call.respond(apiFrameworkFun(hashMapOf("token" to token)))
        } else throw UnauthorizedException()

    }
}