package com.ntihs_fk.router

import com.google.gson.Gson
import com.ntihs_fk.data.Article
import com.ntihs_fk.database.ArticleTable
import com.ntihs_fk.drawImage.draw
import com.ntihs_fk.functions.*
import com.ntihs_fk.socialSoftware.discord.DiscordConfig
import com.ntihs_fk.socialSoftware.discord.discordPost
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.apache.tika.Tika
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

fun Route.post(testing: Boolean) {


    post("/api/post") {
        val article = call.receiveMultipart()
        var fileName: String? = null
        var text: String? = null
        var textImageType = "default"

        article.forEachPart { part ->
            when (part) {

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
                    } else throw BadRequestException("This file not image")
                }

                // 貼文內容
                is PartData.FormItem -> {
                    when (part.name) {
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
                    val data = ArticleTable.insert {
                        it[this.text] = text!!
                        it[this.image] = fileName
                        it[this.textImage] = drawImageFileName
                    }.resultedValues ?: throw Error("Insert error")

                    // post discord
                    for (i in data) {
                        discordPost(
                            discordConfig.voteChannelWebhook,
                            i[ArticleTable.text],
                            i[ArticleTable.textImage],
                            i[ArticleTable.id]
                        )
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

            val data = ArticleTable.select {
                ArticleTable.vote.eq(true)
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

    get("/post/{id}") {

        val id = call.parameters["id"]?.toInt() ?: throw BadRequestException("Missing parameter")
        var data: ResultRow? = null

        transaction {
            data = ArticleTable.select {
                ArticleTable.id.eq(id)
            }.firstOrNull()
        }

        if (data == null) throw NotFoundException()
        else
            call.respondHtml {
                head {
                    meta("og:title", "靠北南工")
                    meta("og:site_name", "靠北南工")
                    meta("og:description", data!![ArticleTable.text])
                    meta(
                        "og:image",
                        "http${if (ssl) "s" else ""}://$domain/image/${data!![ArticleTable.textImage]}.jpg"
                    )
                    meta("og:url", "http${if (ssl) "s" else ""}://$domain/post/$id")
                    meta("og:type", "website")
                    link {
                        href = ""
                    }
                    script {
                        src = ""
                    }
                }
                body {
                    div {
                        this.id = "root"
                        p {
                            +Article(
                                data!![ArticleTable.id],
                                data!![ArticleTable.time].millis,
                                data!![ArticleTable.text],
                                data!![ArticleTable.image],
                                data!![ArticleTable.textImage]
                            ).toString()
                        }
                    }
                }
            }
    }

    get("/api/post/{id}") {

    }
}