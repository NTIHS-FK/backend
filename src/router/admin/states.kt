package com.ntihs_fk.router.admin

import com.google.gson.Gson
import com.ntihs_fk.data.CPULoad
import com.ntihs_fk.data.MemoryData
import com.ntihs_fk.data.StatesData
import com.ntihs_fk.data.ThreadData
import com.ntihs_fk.util.apiFrameworkFun
import com.sun.management.OperatingSystemMXBean
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import java.lang.management.ManagementFactory
import java.util.*

fun Route.states() {

    val gson = Gson()

    webSocket("/api/states") {
        while (true) {
            val lRuntime = Runtime.getRuntime()
            val thread = ManagementFactory.getThreadMXBean()
            val system = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
            val freeMemory = lRuntime.freeMemory() / 1024 / 1024
            val totalMemory = lRuntime.totalMemory() / 1024 / 1024
            val states = StatesData(
                CPULoad(
                    system.processCpuLoad * 100
                ),
                MemoryData(
                    totalMemory - freeMemory,
                    lRuntime.maxMemory() / 1024 / 1024,
                    totalMemory,
                    freeMemory
                ),
                ThreadData(
                    thread.peakThreadCount,
                    thread.threadCount
                ),
                Date().time
            )

            send(gson.toJson(states))
            delay(1_000)
        }
    }

    post("/api/call/gc") {
        System.gc()
        call.respond(apiFrameworkFun(null))
    }
}