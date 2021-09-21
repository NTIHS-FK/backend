package com.ntihs_fk

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.Login
import com.ntihs_fk.database.initDatabase
import com.ntihs_fk.error.UnauthorizedException
import com.ntihs_fk.functions.*
import com.ntihs_fk.router.loginSystem.login
import com.ntihs_fk.router.post
import com.ntihs_fk.router.vote
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val myRealm: String = if (testing) {
        "Access to login"
    } else {
        System.getenv("jwt_realm") ?: "Access to login"
    }

    if (!testing) {
        initDatabase(log)
        init(log)
    }

    install(Sessions) {
        cookie<Login>("SessionId", directorySessionStorage(File(".sessions"), cached = true))
    }

    install(StatusPages) {

        status(*HttpStatusCode.allStatusCodes.toTypedArray()) {
            call.respond(
                HttpStatusCode(it.value, it.description),
                apiFrameworkFun(null, true, it.description)
            )
        }

        exception<BadRequestException> {
            call.respond(
                HttpStatusCode.BadRequest,
                apiFrameworkFun(null, true, it.message)
            )
        }

        exception<UnauthorizedException> {
            call.respond(
                HttpStatusCode.Unauthorized,
                apiFrameworkFun(null, true, it.message)
            )
        }

        exception<Throwable> {
            call.respond(
                HttpStatusCode.InternalServerError,
                apiFrameworkFun(null, true, it.message)
            )
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }

    routing {
        login()
        post(testing)
        vote(testing)
    }
}
