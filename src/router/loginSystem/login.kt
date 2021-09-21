package com.ntihs_fk.router.loginSystem

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.Login
import com.ntihs_fk.data.SignIn
import com.ntihs_fk.data.User
import com.ntihs_fk.database.UserTable
import com.ntihs_fk.error.UnauthorizedException
import com.ntihs_fk.functions.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date


fun Route.login() {


    post("/api/login") {
        val sessionToken = call.sessions.get<Login>()

        if (sessionToken != null) {
            call.respond(apiFrameworkFun(sessionToken))
            return@post
        }

        val user = call.receive<User>()
        var userData: ResultRow? = null

        if (user.nameOrEmail == null || user.password == null)
            throw BadRequestException("Missing parameter")

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
                .withIssuer(issuer)
                .withAudience(audience)
                .withClaim("username", userData!![UserTable.name])
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(secret))

            call.sessions.set(Login(token))
            call.respond(apiFrameworkFun(hashMapOf("token" to token)))
        } else throw UnauthorizedException()

    }

    post("/api/sign-up") {
        val user = call.receive<SignIn>()

        if (user.email == null || user.password == null || user.name == null)
            throw BadRequestException("Missing parameter")

        // check email and name

        transaction {
            if (
                UserTable.select {
                    UserTable.email.eq(user.email).and(
                        UserTable.name.eq(user.name)
                    )
                }.firstOrNull() != null
            )
                throw UnauthorizedException("With use email or name")
        }

        // email verify

        // add data to the database

        transaction {
            UserTable.insert {
                it[name] = user.name
                it[email] = user.email
                it[hashcode] = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
            }
        }

        call.respond(user)
    }

    post("/api/log-out") {
        call.sessions.clear<Login>()
        call.respond(apiFrameworkFun(null))
    }
}