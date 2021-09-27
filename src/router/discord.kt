package com.ntihs_fk.router

import com.ntihs_fk.functions.websocketServer
import io.ktor.routing.*
import io.ktor.websocket.*

fun Route.discord() {
    webSocket("/post/broadcast") {
        websocketServer.addUser(this)
    }
}