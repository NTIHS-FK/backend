package com.ntihs_fk.router

import com.ntihs_fk.data.Article
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.drawImage.draw
import com.ntihs_fk.functions.apiFrameworkFun
import com.ntihs_fk.functions.randomString
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.apache.tika.Tika
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

fun Route.post(testing: Boolean) {
    post("/api/post") {
        if (call.request.contentType().contentType != "multipart")
            throw BadRequestException("Error request")
        val article = call.receiveMultipart()
        var fileName: String? = null
        var text: String? = null
        var textImageType = "default"

        article.forEachPart { part ->
            when(part) {
                // upload image file
                is PartData.FileItem -> {
                    if (fileName != null) throw BadRequestException("Multipart error")
                    val fileBytes = part.streamProvider().readBytes()
                    val fileType = Tika().detect(fileBytes)

                    fileName = Date().time.toString() + randomString() + part.originalFileName as String

                    call.application.log.info(fileType)

                    if (fileType.startsWith("image")) {
                        if (!testing)
                            File("./img/$fileName").writeBytes(fileBytes)
                    }
                    else throw BadRequestException("This file not image")
                }
                // 貼文內容
                is PartData.FormItem -> {
                    when(part.name) {
                        "text" -> text = part.value
                        "textImageType" -> textImageType = part.value
                    }
                }
                else -> throw BadRequestException("Multipart error")
            }
        }
        if (text == null) throw BadRequestException("Missing text")
        else {
            // draw image
            val drawImageFileName = draw(textImageType)(text!!)
            // database
            if (!testing)
                transaction {
                    ArticleTable.insert {
                        it[this.text] = text!!
                        it[this.image] = fileName.toString()
                        it[this.textImageType] = drawImageFileName
                    }
                }
            // log OAO
            call.application.log.info(
                "[${call.request.host()}] " +
                        "Say:\n\u001B[34m[Text]\u001b[0m \n\u001B[35m$text\u001B[0m\n" +
                        "\u001B[34m[Image]\u001B[0m ${fileName ?: "No image"} " +
                        "\u001B[34m[Text Image]\u001B[0m $drawImageFileName"
            )

            // respond api
            call.respond(apiFrameworkFun(null))
        }
    }

    get("/api/posts") {
        val rePots = mutableListOf<Article>()
        transaction {
            val data = ArticleTable.select{
                ArticleTable.vote.eq(true)
            }

            for(i in data) {
                rePots.add(Article(
                    i[ArticleTable.id],
                    i[ArticleTable.time].millis,
                    i[ArticleTable.text],
                    i[ArticleTable.image],
                    i[ArticleTable.textImageType]
                ))
            }
        }
        call.respond(apiFrameworkFun(rePots))
    }
}