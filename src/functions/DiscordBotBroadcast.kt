package com.ntihs_fk.functions

import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
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
                    i.send(Frame.Text(gson.toJson(data)))
                }
            } catch (error: Error) {
                users.remove(i)
            }
        }
    }
}

val websocketServer = DiscordBotBroadcast()