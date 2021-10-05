package com.ntihs_fk.util

import com.google.gson.Gson
import com.ntihs_fk.data.BroadcastConnectData
import com.ntihs_fk.data.BroadcastUserData
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import java.util.*

class Broadcast {
    private val users: MutableList<BroadcastConnectData> = mutableListOf()
    private val gson: Gson = Gson()

    fun addUser(user: DefaultWebSocketServerSession) {
        users.add(
            BroadcastConnectData(
                user,
                Date().time,
                user.call.request.host()
            )
        )
    }

    fun <T> broadcast(data: T) {
        for (i in users) {
            try {
                runBlocking {
                    i.webSocketServerSession.application.log.info(
                        "send message to ${i.host}"
                    )
                    i.webSocketServerSession.send(Frame.Text(gson.toJson(data)))
                }
            } catch (error: Throwable) {
                i.webSocketServerSession.application.log.error("${i.host} $error")
                users.remove(i)
            }
        }
    }

    val usersData get() = run {
        users.map {
            BroadcastUserData(it.host, it.time)
        }
    }
}

val webSocketServer = Broadcast()