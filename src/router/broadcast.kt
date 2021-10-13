package com.ntihs_fk.router

import com.ntihs_fk.util.webSocketServer
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Route.broadcast() {
    webSocket("/post/broadcast") {
        webSocketServer.addUser(this)
        application.log.info("${call.request.host()} connected!")
        send("ok")

        for(frame in incoming) {
            send("ok")
        }
    }
}