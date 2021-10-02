package com.ntihs_fk.router.loginSystem.update

import at.favre.lib.crypto.bcrypt.BCrypt
import com.ntihs_fk.data.LoginTokenData
import com.ntihs_fk.data.UpdateEmailData
import com.ntihs_fk.data.UpdatePasswordData
import com.ntihs_fk.database.UserTable
import com.ntihs_fk.error.UnauthorizedRequestException
import com.ntihs_fk.util.JWTBlacklist
import com.ntihs_fk.util.apiFrameworkFun
import com.ntihs_fk.util.verifyPassword
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.update() {

    patch("/email") {
        val updateData = call.receive<UpdateEmailData>()
        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()

        if (updateData.newEmail == null || updateData.password == null) throw BadRequestException("Missing parameter")

        val verifyData = verifyPassword(updateData.password, username)

        if (verifyData.verify) {
            transaction {
                UserTable.update({ UserTable.name eq username }) {
                    it[email] = updateData.newEmail
                }
            }
        } else throw UnauthorizedRequestException()

        call.respond(apiFrameworkFun(null))
    }

    patch("/password") {
        val updateData = call.receive<UpdatePasswordData>()
        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()

        if (updateData.password == null) throw BadRequestException("Missing parameter")

        val verifyData = verifyPassword(updateData.password, username)

        if (verifyData.verify) {
            transaction {
                UserTable.update({ UserTable.name eq username }) {
                    it[hashcode] = BCrypt.withDefaults().hashToString(12, updateData.password.toCharArray())
                }
            }
        } else throw UnauthorizedRequestException()

        call.sessions.clear<LoginTokenData>()
        JWTBlacklist.addBlacklistTokenId(principal.jwtId!!, principal.expiresAt!!)
        call.respondRedirect("/")
    }
}