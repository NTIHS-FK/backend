package com.ntihs_fk.router

import com.ntihs_fk.data.Article
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.functions.apiFrameworkFun
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
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
            call.respond("a")
        }
    }

}