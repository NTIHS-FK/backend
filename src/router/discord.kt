package com.ntihs_fk.router

import com.ntihs_fk.util.websocketServer
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.async

fun Route.discord() {
    webSocket("/post/broadcast") {
        websocketServer.addUser(this)
        application.log.info("${call.request.host()} connected!")
        send("ok")

        for(frame in incoming) {
            send("ok")
        }
    }
}