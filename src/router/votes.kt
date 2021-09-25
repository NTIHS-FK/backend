package com.ntihs_fk.router

import com.ntihs_fk.data.Article
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.database.VoteTable
import com.ntihs_fk.functions.apiFrameworkFun
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.vote(testing: Boolean) {
    get("/api/votes") {
        val rePots = mutableListOf<Article>()

        transaction {
            val data = ArticleTable.select {
                ArticleTable.vote.eq(false)
            }

            for (i in data) {
                rePots.add(
                    Article(
                        i[ArticleTable.id],
                        i[ArticleTable.time].millis,
                        i[ArticleTable.text],
                        i[ArticleTable.image],
                        i[ArticleTable.textImage]
                    )
                )
            }
        }

        call.respond(apiFrameworkFun(rePots))
    }

    get("/api/vote/{id}/count") {
        val id = call.parameters["id"] ?: throw BadRequestException("Missing parameter")
        var count = 0
        transaction {
            count = VoteTable.select {
                VoteTable.postId.eq(id.toInt())
            }.filterNotNull().size
        }
        call.respond(apiFrameworkFun(mapOf("count" to count)))
    }

    authenticate("auth-jwt") {
        post("/api/vote/{id}") {
            val id = call.parameters["id"]
            val voteQuery = call.request.queryParameters["vote"]
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()

            if (id == null && voteQuery == null) throw BadRequestException("Missing parameter")

            transaction {
                if (
                    VoteTable.select {
                        VoteTable.name.eq(username)
                    }.firstOrNull() != null
                ) throw BadRequestException("Voted")

                VoteTable.insert {
                    it[name] = username
                    it[postId] = id!!.toInt()
                    it[vote] = voteQuery.toBoolean()
                }
            }

            call.respond(apiFrameworkFun(null))
        }
    }

}