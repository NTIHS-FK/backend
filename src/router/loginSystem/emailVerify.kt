package com.ntihs_fk.router.loginSystem

import com.ntihs_fk.database.UserTable
import com.ntihs_fk.util.emailVerifyFun
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.emailVerify() {

    get("/email-verify") {
        val code = call.request.queryParameters["code"] ?: throw BadRequestException("Missing parameter")
        val email = emailVerifyFun.parserJWSToken(code)

        transaction {
            UserTable.update({ UserTable.email eq email }) {
                it[this.verify] = true
            }
        }

        call.respondRedirect("/")
    }
}