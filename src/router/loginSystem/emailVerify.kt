package com.ntihs_fk.router.loginSystem

import com.ntihs_fk.functions.emailVerifyFun
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*

fun Route.emailVerify() {


    get("/email-verify") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")

        emailVerifyFun.parserJWSToken(code)
    }
}