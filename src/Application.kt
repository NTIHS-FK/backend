package com.ntihs_fk

import com.ntihs_fk.database.initDatabase
import com.ntihs_fk.router.post
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.client.*
import twitter4j.StatusUpdate
import twitter4j.TwitterFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    initDatabase()
    install(Sessions) {
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            val twitter = TwitterFactory.getSingleton()
            val statusUpdate = StatusUpdate("a")
            twitter.updateStatus(statusUpdate)
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }

    routing {
        post()
    }
}
