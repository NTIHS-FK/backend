package com.ntihs_fk.router

import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.drawImage.draw
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.textImage() {
    get("/textImage/{id}") {
        val id = call.parameters["id"]
        var article: ResultRow? = null
        if (!id.isNullOrEmpty()) {
            transaction {
                article = ArticleTable.select {
                    ArticleTable.id eq id.toInt()
                }.firstOrNull() ?: throw NotFoundException("no id")

            }
            if (article != null) {
                call.respond(draw("default")(article!![ArticleTable.text]))
            }
        }

    }
}