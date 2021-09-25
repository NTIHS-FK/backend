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

    authenticate("auth-jwt") {
        post("/api/vote/{id}") {
            val id = call.parameters["id"]
            val voteQuery = call.request.queryParameters["vote"]
            if (id == null && voteQuery == null) throw BadRequestException("Missing parameter")
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()

            transaction {
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