package com.ntihs_fk.router

import com.ntihs_fk.data.ArticleData
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.database.VoteTable
import com.ntihs_fk.util.apiFrameworkFun
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.vote() {
    get("/api/votes") {
        val rePots = mutableListOf<ArticleData>()

        transaction {
            val data = ArticleTable.select {
                ArticleTable.votingThreshold.eq(false)
            }

            for (i in data) {
                rePots.add(
                    ArticleData(
                        i[ArticleTable.id],
                        i[ArticleTable.time].millis,
                        i[ArticleTable.text],
                        i[ArticleTable.image],
                        i[ArticleTable.textImage],
                        i[ArticleTable.votingThreshold]
                    )
                )
            }
        }

        call.respond(apiFrameworkFun(rePots))
    }

    get("/api/vote/{id}/count") {
        val id = call.parameters["id"] ?: throw BadRequestException("Missing parameter")
        var approve = 0
        var against = 0

        transaction {
            approve = VoteTable.select {
                VoteTable.postId.eq(id.toInt()).and(
                    VoteTable.vote.eq(true)
                )
            }.filterNotNull().size

            against = VoteTable.select {
                VoteTable.postId.eq(id.toInt()).and(
                    VoteTable.vote.eq(false)
                )
            }.filterNotNull().size
        }

        call.respond(
            apiFrameworkFun(
                mapOf(
                    "approve" to approve,
                    "against" to against
                )
            )
        )
    }

    authenticate("auth-jwt") {
        post("/api/vote/{id}") {
            val id = call.parameters["id"]
            val voteQuery = call.request.queryParameters["vote"]
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val verify = principal.payload.getClaim("verify").asBoolean()
            val type = principal.payload.getClaim("type").asString()

            if (id == null && voteQuery == null) throw BadRequestException("Missing parameter")
            if (!verify) throw BadRequestException("Email not verify")

            transaction {
                if (
                    VoteTable.select {
                        VoteTable.name.eq(username).and(
                            VoteTable.loginType.eq(type)
                        )
                    }.firstOrNull() != null
                ) throw BadRequestException("Voted")

                VoteTable.insert {
                    it[name] = username
                    it[postId] = id!!.toInt()
                    it[vote] = voteQuery.toBoolean()
                    it[loginType] = type
                }
            }

            call.respond(apiFrameworkFun(null))
        }
    }

}