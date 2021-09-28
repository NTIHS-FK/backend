package com.ntihs_fk.router

import com.ntihs_fk.functions.websocketServer
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Route.discord() {
    webSocket("/post/broadcast") {
        websocketServer.addUser(this)
        send("You are connected!")
        for(frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            send("idk $receivedText")
        }
    }
}