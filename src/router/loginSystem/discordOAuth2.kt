package com.ntihs_fk.router.loginSystem

import com.ntihs_fk.functions.DiscordOAuth2
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.discord() {
    get("/api/discord/authorize") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        call.respond(DiscordOAuth2.exchangeCode(code))
    }
}