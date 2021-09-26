package com.ntihs_fk

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ntihs_fk.data.Login
import com.ntihs_fk.database.initDatabase
import com.ntihs_fk.error.UnauthorizedException
import com.ntihs_fk.functions.*
import com.ntihs_fk.router.loginSystem.discord
import com.ntihs_fk.router.loginSystem.emailVerify
import com.ntihs_fk.router.loginSystem.login
import com.ntihs_fk.router.post
import com.ntihs_fk.router.vote
import io.jsonwebtoken.JwtException
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
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.io.File

fun main(args: Array<String>) {

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8080
        }

        if (Config.ssl) {
            val keyStoreFile = File("build/keystore.jks")
            val keystore = generateCertificate(
                file = keyStoreFile,
                keyAlias = "sampleAlias",
                keyPassword = "foobar",
                jksPassword = "foobar"
            )

            sslConnector(
                keyStore = keystore,
                keyAlias = "sampleAlias",
                keyStorePassword = { "foobar".toCharArray() },
                privateKeyPassword = { "foobar".toCharArray() }) {
                port = 8443
                keyStorePath = keyStoreFile
            }
        }

        module(Application::module)
    }

    embeddedServer(Netty, environment).start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val myRealm: String = System.getenv("jwt_realm") ?: "Access to login"

    if (!testing) {
        initDatabase(log)
        init(log)
    }

    if (Config.ssl) install(HSTS)

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

        exception<JwtException> {
            call.respond(
                HttpStatusCode.Unauthorized,
                apiFrameworkFun(null, true, "token error")
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
                .require(Algorithm.HMAC256(Config.secret))
                .withAudience(Config.audience)
                .withIssuer(Config.issuer)
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
        discord()
        emailVerify()
    }
}
