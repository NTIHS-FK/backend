package com.ntihs_fk.router.loginSystem

import com.ntihs_fk.database.UserTable
import com.ntihs_fk.util.apiFrameworkFun
import com.ntihs_fk.util.emailVerifyFun
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
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

    authenticate("auth-jwt") {

        post("/api/resend-email") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val verify = principal.payload.getClaim("verify").asBoolean()
            lateinit var email: String

            transaction {
                email = UserTable.select {
                    UserTable.name.eq(username)
                }.first()[UserTable.email]
            }

            if (!verify) {
                emailVerifyFun.sendEmail(email)
                call.respond(apiFrameworkFun(null))
            } else throw BadRequestException("Verified")

        }
    }
}