package com.ntihs_fk.router.admin

import com.google.gson.Gson
import com.ntihs_fk.util.apiFrameworkFun
import com.ntihs_fk.util.webSocketServer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay

fun Route.adminBroadcastConnect() {
    val gson = Gson()

    webSocket("/api/broadcastConnect") {
        while(true) {
            send(gson.toJson(webSocketServer.usersData))

            delay(5_000)
        }
    }

    delete("/api/broadcastConnect/{host}") {
        val host = call.parameters["host"] ?: throw BadRequestException("Missing parameter")

        webSocketServer.delUser(host)

        call.respond(apiFrameworkFun(null))
    }
}