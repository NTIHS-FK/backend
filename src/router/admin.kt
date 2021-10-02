package com.ntihs_fk.router

import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.functions.apiFrameworkFun
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.admin() {
    authenticate("auth-jwt-admin") {
        route("/admin") {
            delete("/api/post/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")

                transaction {
                    ArticleTable.deleteWhere {
                        ArticleTable.id.eq(id)
                    }
                }

                call.respond(apiFrameworkFun(null))
            }
        }
    }
}