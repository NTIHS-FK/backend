package com.ntihs_fk

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.cli.Main
import com.ntihs_fk.data.LoginTokenData
import com.ntihs_fk.database.initDatabase
import com.ntihs_fk.error.ForbiddenRequestException
import com.ntihs_fk.error.UnauthorizedRequestException
import com.ntihs_fk.router.*
import com.ntihs_fk.router.loginSystem.discordOAuth2
import com.ntihs_fk.router.loginSystem.emailVerify
import com.ntihs_fk.router.loginSystem.googleOAuth2
import com.ntihs_fk.router.loginSystem.login
import com.ntihs_fk.util.Config
import com.ntihs_fk.util.JWTBlacklist
import com.ntihs_fk.util.apiFrameworkFun
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

    install(CORS) {
        anyHost()
    }

    install(StatusPages) {

        status(HttpStatusCode.NotFound) {
            log.error(this.context.request.host())
            // 404 page
        }

        exception<NoSuchFileException> {
            call.respond(
                HttpStatusCode.NotFound,
                apiFrameworkFun(null, true, "No Such File ${it.message}")
            )
        }

        exception<BadRequestException> {
            call.respond(
                HttpStatusCode.BadRequest,
                apiFrameworkFun(null, true, it.message)
            )
        }

        exception<UnauthorizedRequestException> {
            call.sessions.clear<LoginTokenData>()
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
                apiFrameworkFun(null, true, "Token error")
            )
        }

        exception<Throwable> {
            log.error(
                "${call.request.host()} Send ---> ${call.request.httpMethod.value} - ${call.request.path()}" +
                        "error message ${it.localizedMessage}"
            )
            call.respond(
                HttpStatusCode.InternalServerError,
                apiFrameworkFun(null, true, "Server Error")
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
        static()
        login()
        post(testing)
        vote()
        admin()
        broadcast()
        if (!Config.discordConfig.disable)
            discordOAuth2()
        if (!Config.gmailConfig.disable)
            emailVerify()
        if (!Config.googleConfig.disable)
            googleOAuth2()
    }
}
