package com.ntihs_fk.router.loginSystem

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*

fun Route.discord() {
    get("/api/discord/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")

    }
}