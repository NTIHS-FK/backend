package com.ntihs_fk.router.admin

import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.socialSoftware.mainPost
import com.ntihs_fk.util.apiFrameworkFun
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.adminPost() {
    delete("/api/post/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")

        transaction {
            ArticleTable.deleteWhere {
                ArticleTable.id.eq(id)
            }
        }

        call.respond(apiFrameworkFun(null))
    }

    put("/api/post/{id}/publish") {
        val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")

        transaction {
            ArticleTable.update({ ArticleTable.id eq id }) {
                it[voting] = true
            }
        }

        mainPost(id)
        call.respond(apiFrameworkFun(null))
    }
}