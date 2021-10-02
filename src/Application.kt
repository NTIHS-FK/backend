package com.ntihs_fk

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.cli.Main
import com.ntihs_fk.data.LoginTokenData
import com.ntihs_fk.database.initDatabase
import com.ntihs_fk.error.ForbiddenRequestException
import com.ntihs_fk.error.UnauthorizedRequestException
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.JWTBlacklist
import com.ntihs_fk.util.apiFrameworkFun
import com.ntihs_fk.router.admin
import com.ntihs_fk.router.discord
import com.ntihs_fk.router.loginSystem.discordOAuth2
import com.ntihs_fk.router.loginSystem.emailVerify
import com.ntihs_fk.router.loginSystem.googleOAuth2
import com.ntihs_fk.router.loginSystem.login
import com.ntihs_fk.router.post
import com.ntihs_fk.router.vote
import io.jsonwebtoken.JwtException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import org.slf4j.event.Level
import java.io.File
import java.time.Duration

fun main(args: Array<String>) = Main().main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val myRealm: String = System.getenv("jwt_realm") ?: "Access to login"

    if (!testing) initDatabase(log)

    if (Config.ssl) install(HSTS)

    install(Sessions) {
    cookie<LoginTokenData>("SessionId", directorySessionStorage(File(".sessions"), cached = true))
    }

    install(StatusPages) {

    val allHttpCode = HttpStatusCode.allStatusCodes.toMutableList()
        allHttpCode.remove(HttpStatusCode.SwitchingProtocols)
        status(*allHttpCode.toTypedArray()) {
            println(it.description)
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

        exception<UnauthorizedRequestException> {
            call.respond(
                HttpStatusCode.Unauthorized,
                apiFrameworkFun(null, true, it.message)
            )
        }

        exception<ForbiddenRequestException> {
            call.respond(
                HttpStatusCode.Forbidden,
                apiFrameworkFun(null, true, it.message)
            )
        }

        exception<JwtException> {
            call.respond(
                HttpStatusCode.Unauthorized,
                apiFrameworkFun(null, true, "token error")
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
        val verifier = JWT
            .require(Algorithm.HMAC256(Config.secret))
            .withIssuer(Config.issuer)
            .build()

        jwt("auth-jwt") {
            realm = myRealm
            verifier(verifier)

            validate { credential ->
                if (JWTBlacklist.isInside(credential.jwtId!!))
                    JWTPrincipal(credential.payload)
                else null
            }
        }

        jwt("auth-jwt-admin") {
            realm = myRealm
            verifier(verifier)

            validate { credential ->
                if (
                    JWTBlacklist.isInside(credential.jwtId!!) &&
                    credential.payload.getClaim("admin")!!.asBoolean()
                )
                    JWTPrincipal(credential.payload)
                else throw ForbiddenRequestException()
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }

    routing {
        login()
        post(testing)
        vote()
        discordOAuth2()
        emailVerify()
        discord()
        googleOAuth2()
        admin()
    }
}
