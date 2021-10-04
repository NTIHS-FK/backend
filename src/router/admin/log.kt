package com.ntihs_fk.router.admin

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Route.log() {

    get("/log/list") {
        val fileList = File("./logs").list()
        call.respond(fileList)
    }

    static {
        files("./logs")
    }
}