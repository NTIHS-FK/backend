package com.ntihs_fk.data

import io.ktor.websocket.*

data class BroadcastConnectData(val webSocketServerSession: DefaultWebSocketServerSession, val time: Long, val host: String)
