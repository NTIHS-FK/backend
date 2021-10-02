package com.ntihs_fk.util

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking

class DiscordBotBroadcast {
    private val users: MutableList<DefaultWebSocketServerSession> = mutableListOf()
    private val gson: Gson = Gson()

    fun addUser(user: DefaultWebSocketServerSession) = users.add(user)

    fun <T> broadcast(data: T) {
        for (i in users) {
            try {
                runBlocking {
                    i.application.log.info("send message to ${i.call.request.host()}")
                    i.send(Frame.Text(gson.toJson(data)))
                }
            } catch (error: Throwable) {
                i.application.log.error("${i.call.request.host()} $error")
                users.remove(i)
            }
        }
    }
}

val websocketServer = DiscordBotBroadcast()